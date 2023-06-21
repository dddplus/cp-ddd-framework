package io.github.design.mybatis.associations;

import io.github.design.CheckTask;
import io.github.design.ShipmentOrder;
import io.github.design.mybatis.ModelMapper;

import javax.inject.Inject;
import java.util.List;

public class TaskOrders implements CheckTask.ShipmentOrders {
    private final CheckTask task;

    public TaskOrders(CheckTask checkTask) {
        this.task = checkTask;
    }

    @Inject
    private ModelMapper modelMapper;

    @Override
    public List<ShipmentOrder> pendingOrders() {
        return modelMapper.findPendingOrdersByTask(task.getTaskNo());
    }
}
