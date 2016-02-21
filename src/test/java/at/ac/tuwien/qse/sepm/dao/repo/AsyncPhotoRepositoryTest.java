package at.ac.tuwien.qse.sepm.dao.repo;

/*
 * Copyright (c) 2015 Lukas Eibensteiner
 * Copyright (c) 2015 Kristoffer Kleine
 * Copyright (c) 2015 Branko Majic
 * Copyright (c) 2015 Enri Miho
 * Copyright (c) 2015 David Peherstorfer
 * Copyright (c) 2015 Marian Stoschitzky
 * Copyright (c) 2015 Christoph Wasylewski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import at.ac.tuwien.qse.sepm.dao.DAOException;
import org.junit.Test;

import static org.junit.Assert.*;

public abstract class AsyncPhotoRepositoryTest extends PhotoRepositoryTest {

    protected abstract AsyncPhotoRepository getObject();

    @Override protected Context getContext() {
        return null;
    }

    @Test
    public void getQueue_nothingModified_returnsEmpty() throws DAOException {
        AsyncPhotoRepository object = getObject();

        assertTrue(object.getQueue().isEmpty());
    }

    @Test
    public void getQueue_updated_returnsUpdateOperation() throws DAOException {
        AsyncPhotoRepository object = getObject();
        object.create(getContext().getFile1(), getContext().getStream1());
        object.completeNext();
        object.update(getContext().getModified1());

        assertEquals(1, object.getQueue().size());
        Operation op = object.getQueue().poll();
        assertEquals(Operation.Kind.UPDATE, op.getKind());
        assertEquals(getContext().getFile1(), op.getFile());
    }

    @Test
    public void getQueue_deleted_returnsDeleteOperation() throws DAOException {
        AsyncPhotoRepository object = getObject();
        object.create(getContext().getFile1(), getContext().getStream1());
        object.completeNext();

        object.delete(getContext().getFile1());
        assertEquals(1, object.getQueue().size());
        Operation op = object.getQueue().poll();
        assertEquals(Operation.Kind.DELETE, op.getKind());
        assertEquals(getContext().getFile1(), op.getFile());
    }

    @Test
    public void addAsyncListener_update_notifies() throws DAOException {
        AsyncPhotoRepository object = getObject();
        MockAsyncListener listener = new MockAsyncListener();
        object.addListener(listener);
        object.create(getContext().getFile1(), getContext().getStream1());
        object.update(getContext().getModified1());

        assertEquals(2, listener.getQueueNotifications().size());
        assertEquals(0, listener.getCompleteNotifications().size());
        assertEquals(0, listener.getErrorNotifications().size());
        MockAsyncListener.OperationNotification notification = listener.getQueueNotifications().get(1);
        assertEquals(object, notification.getRepository());
        assertEquals(Operation.Kind.UPDATE, notification.getOperation().getKind());
        assertEquals(getContext().getFile1(), notification.getOperation().getFile());
    }

    @Test
    public void addAsyncListener_delete_notifies() throws DAOException {
        AsyncPhotoRepository object = getObject();
        MockAsyncListener listener = new MockAsyncListener();
        object.addListener(listener);
        object.create(getContext().getFile1(), getContext().getStream1());
        object.delete(getContext().getFile1());

        assertEquals(2, listener.getQueueNotifications().size());
        assertEquals(0, listener.getCompleteNotifications().size());
        assertEquals(0, listener.getErrorNotifications().size());
        MockAsyncListener.OperationNotification notification = listener.getQueueNotifications().get(1);
        assertEquals(object, notification.getRepository());
        assertEquals(Operation.Kind.DELETE, notification.getOperation().getKind());
        assertEquals(getContext().getFile1(), notification.getOperation().getFile());
    }

    @Test
    public void addAsyncListener_completeNext_notifies() throws DAOException {
        AsyncPhotoRepository object = getObject();
        MockAsyncListener listener = new MockAsyncListener();
        object.addListener(listener);
        object.create(getContext().getFile1(), getContext().getStream1());

        assertEquals(1, object.getQueue().size()); // safeguard, since completeNext blocks
        object.completeNext();
        assertEquals(1, listener.getQueueNotifications().size());
        assertEquals(1, listener.getCompleteNotifications().size());
        assertEquals(0, listener.getErrorNotifications().size());
        MockAsyncListener.OperationNotification notification = listener.getCompleteNotifications().get(0);
        assertEquals(object, notification.getRepository());
        assertEquals(Operation.Kind.READ, notification.getOperation().getKind());
        assertEquals(getContext().getFile1(), notification.getOperation().getFile());
    }
}
