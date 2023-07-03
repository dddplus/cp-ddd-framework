package ddd.plus.showcase.wms.domain.base;

import ddd.plus.showcase.wms.domain.common.WmsException;
import io.github.dddplus.buddy.specification.ISpecification;
import io.github.dddplus.buddy.specification.Notification;
import io.github.dddplus.model.IBag;
import lombok.Getter;

import java.util.List;

public abstract class ListBag<Entity> implements IBag {
    @Getter
    protected List<Entity> items;

    public final int size() {
        return items.size();
    }

    public final boolean isEmpty() {
        return size() == 0;
    }

    protected Entity anyItem() {
        return items.iterator().next();
    }

    public final void assureSatisfied(ISpecification<Entity> specification) throws WmsException {
        Notification notification = Notification.build();
        for (Entity item : items) {
            if (!specification.isSatisfiedBy(item, notification)) {
                throw new WmsException(notification.first());
            }
        }
    }
}
