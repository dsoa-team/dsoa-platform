package br.ufpe.cin.dsoa.platform.service.selector;

import java.util.ArrayList;
import java.util.List;


public class RollingList
{
  private List<DataValue> items;
  private int capacity;

  public RollingList(int capacity)
  {
    this.capacity = capacity;
    this.items = new ArrayList<DataValue>();
  }

  public void add(DataValue e) {
    if (this.items.size() > this.capacity - 1) {
      this.items.remove(0);
    }
    this.items.add(e);
  }

  public List<DataValue> getList() {
    return this.items;
  }

}