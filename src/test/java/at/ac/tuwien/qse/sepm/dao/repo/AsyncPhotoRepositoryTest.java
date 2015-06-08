package at.ac.tuwien.qse.sepm.dao.repo;

import org.junit.Test;

import static org.junit.Assert.*;

public abstract class AsyncPhotoRepositoryTest extends PhotoRepositoryTest {

    protected abstract AsyncPhotoRepository getObject();

    @Override protected Context getContext() {
        return null;
    }

    @Test
    public void getQueue_nothingModified_returnsEmpty() throws PersistenceException {
        AsyncPhotoRepository object = getObject();

        assertTrue(object.getQueue().isEmpty());
    }

    @Test
    public void getQueue_updated_returnsUpdateOperation() throws PersistenceException {
        AsyncPhotoRepository object = getObject();
        object.create(getContext().getFile1(), getContext().getStream1());
        object.update(getContext().getModified1());

        assertEquals(1, object.getQueue().size());
        Operation op = object.getQueue().poll();
        assertEquals(Operation.Kind.UPDATE, op.getKind());
        assertEquals(getContext().getFile1(), op.getFile());
    }

    @Test
    public void getQueue_deleted_returnsDeleteOperation() throws PersistenceException {
        AsyncPhotoRepository object = getObject();
        object.create(getContext().getFile1(), getContext().getStream1());
        object.delete(getContext().getFile1());

        assertEquals(1, object.getQueue().size());
        Operation op = object.getQueue().poll();
        assertEquals(Operation.Kind.DELETE, op.getKind());
        assertEquals(getContext().getFile1(), op.getFile());
    }

    @Test
    public void addListener_update_notifies() throws PersistenceException {
        AsyncPhotoRepository object = getObject();
        MockAsyncListener listener = new MockAsyncListener();
        object.addListener(listener);
        object.create(getContext().getFile1(), getContext().getStream1());
        object.update(getContext().getModified1());

        assertEquals(1, listener.getAddNotifications().size());
        assertEquals(0, listener.getCompleteNotifications().size());
        assertEquals(0, listener.getErrorNotifications().size());
        MockAsyncListener.OperationNotification notification = listener.getAddNotifications().get(0);
        assertEquals(object, notification.getRepository());
        assertEquals(Operation.Kind.UPDATE, notification.getOperation().getKind());
        assertEquals(getContext().getFile1(), notification.getOperation().getFile());
    }

    @Test
    public void addAsyncListener_delete_notifies() throws PersistenceException {
        AsyncPhotoRepository object = getObject();
        MockAsyncListener listener = new MockAsyncListener();
        object.addListener(listener);
        object.create(getContext().getFile1(), getContext().getStream1());
        object.delete(getContext().getFile1());

        assertEquals(1, listener.getAddNotifications().size());
        assertEquals(0, listener.getCompleteNotifications().size());
        assertEquals(0, listener.getErrorNotifications().size());
        MockAsyncListener.OperationNotification notification = listener.getAddNotifications().get(0);
        assertEquals(object, notification.getRepository());
        assertEquals(Operation.Kind.DELETE, notification.getOperation().getKind());
        assertEquals(getContext().getFile1(), notification.getOperation().getFile());
    }

    @Test
    public void addAsyncListener_completeNext_notifies() throws PersistenceException {
        AsyncPhotoRepository object = getObject();
        MockAsyncListener listener = new MockAsyncListener();
        object.addListener(listener);
        object.create(getContext().getFile1(), getContext().getStream1());
        object.delete(getContext().getFile1());

        assertEquals(1, object.getQueue().size()); // safeguard, since completeNext blocks
        object.completeNext();
        assertEquals(1, listener.getAddNotifications().size());
        assertEquals(1, listener.getCompleteNotifications().size());
        assertEquals(0, listener.getErrorNotifications().size());
        MockAsyncListener.OperationNotification notification = listener.getCompleteNotifications().get(0);
        assertEquals(object, notification.getRepository());
        assertEquals(Operation.Kind.DELETE, notification.getOperation().getKind());
        assertEquals(getContext().getFile1(), notification.getOperation().getFile());
    }
}
