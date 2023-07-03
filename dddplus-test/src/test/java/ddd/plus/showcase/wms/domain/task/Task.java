package ddd.plus.showcase.wms.domain.task;

import ddd.plus.showcase.wms.domain.base.BaseAggregateRoot;
import ddd.plus.showcase.wms.domain.carton.CartonItem;
import ddd.plus.showcase.wms.domain.carton.CartonItemBag;
import ddd.plus.showcase.wms.domain.common.Operator;
import ddd.plus.showcase.wms.domain.common.WarehouseNo;
import ddd.plus.showcase.wms.domain.order.Order;
import ddd.plus.showcase.wms.domain.order.OrderBag;
import ddd.plus.showcase.wms.domain.order.OrderNo;
import ddd.plus.showcase.wms.domain.task.dict.TaskStatus;
import ddd.plus.showcase.wms.domain.task.hint.ConfirmQtyHint;
import ddd.plus.showcase.wms.domain.task.hint.TaskDirtyHint;
import io.github.dddplus.dsl.*;
import io.github.dddplus.model.IUnboundedDomainModel;
import io.github.dddplus.model.association.HasMany;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Set;

/**
 * 复核任务.
 */
@EqualsAndHashCode(exclude = "memento")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Getter
@KeyRelation(whom = ContainerBag.class, type = KeyRelation.Type.HasOne)
public class Task extends BaseAggregateRoot<Task> implements IUnboundedDomainModel {
    private Long id;

    private TaskNo taskNo;
    private Integer priority;
    @KeyElement(types = KeyElement.Type.Lifecycle, byType = true)
    private TaskStatus status;
    @KeyElement(types = KeyElement.Type.Location, byType = true)
    private PlatformNo platformNo;
    // 该复核任务由哪一个操作员完成：1个任务只能1人完成
    private Operator operator;
    private WarehouseNo warehouseNo;

    private ContainerBag containerBag;

    // associations
    private TaskOrders orders;
    private TaskCartonItems cartonItems;

    /**
     * 针对关联关系显式建模.
     *
     * <p>即，关联关系本身也是模型：domain层是接口，infrastructure供应实现.</p>
     */
    public interface TaskOrders extends HasMany<Order> {
        /**
         * 当前复核任务的待复核的所有出库单.
         */
        OrderBag pendingOrders();

        Order pendingOrder(OrderNo orderNo);
    }

    public interface TaskCartonItems extends HasMany<CartonItem> {
        /**
         * 该任务已经装箱的货品明细.
         */
        CartonItemBag cartonItemBag();
    }

    public Set<OrderNo> orderNoSet() {
        return containerBag.orderNoSet();
    }

    @KeyRule
    public int totalSku() {
        return containerBag.totalSku();
    }

    @KeyRule
    public BigDecimal totalQty() {
        return containerBag.totalQty();
    }

    @KeyBehavior
    public void bind(Operator operator, PlatformNo platformNo) {
        this.platformNo = platformNo;
        this.operator = operator;
        dirty(new TaskDirtyHint(this).dirty("operator", "platform_no"));
    }

    @KeyBehavior
    public void confirmQty(BigDecimal qty, Operator operator, PlatformNo platformNo) {
        this.platformNo = platformNo;
        this.operator = operator;
        containerBag.pendingItemBag().confirmQty(qty);
        dirty(new ConfirmQtyHint(this));
    }

}
