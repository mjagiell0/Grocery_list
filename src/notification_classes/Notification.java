package notification_classes;

import java.io.Serial;
import java.io.Serializable;

public class Notification implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Object[] data;
    private NotificationCode code;

    public Notification() {}

    public void setData(Object[] data) {
        this.data = data;
    }

    public void setCode(NotificationCode code) {
        this.code = code;
    }

    public Object[] getData() {
        return data;
    }

    public NotificationCode getCode() {
        return code;
    }
}
