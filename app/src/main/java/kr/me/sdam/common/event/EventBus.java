package kr.me.sdam.common.event;

import com.squareup.otto.Bus;

/**
 * Created by KMS on 2017-01-10.
 */
public final class EventBus {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private EventBus() {
        // No instances.
    }
}