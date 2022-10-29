package dgtc.entity.dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/** @author hunglv */
public class PageImpl<T> implements Page<T> {

  private static final long serialVersionUID = 867755909294344406L;

  private long total;
  private List<T> listContent;
  private int page;
  private int size;

  public PageImpl(List<T> listContent, long total, int page, int size) {
    this.listContent = listContent;
    this.total = total;
    this.page = page;
    this.size = size;
  }

  @Override
  public int getTotalPages() {
    return getSize() == 0 ? 1 : (int) Math.ceil((double) total / (double) getSize());
  }

  @Override
  public long getTotalElements() {
    return total;
  }

  @Override
  public int getNumber() {
    return page;
  }

  @Override
  public int getSize() {
    return size;
  }

  @Override
  public int getNumberOfElements() {
    return listContent.size();
  }

  @Override
  public List<T> getContent() {
    return listContent;
  }

  @Override
  public boolean hasContent() {
    return !listContent.isEmpty();
  }

  @Override
  public Sort getSort() {
    return null;
  }

  @Override
  public boolean isFirst() {
    return page == 1;
  }

  @Override
  public boolean isLast() {
    return page == getTotalPages();
  }

  @Override
  public boolean hasNext() {
    return !isLast();
  }

  @Override
  public boolean hasPrevious() {
    return !isFirst();
  }

  @Override
  public Pageable nextPageable() {
    return null;
  }

  @Override
  public Pageable previousPageable() {
    return null;
  }

  @Override
  public <U> Page<U> map(Function<? super T, ? extends U> converter) {
    return null;
  }

  @Override
  public Iterator<T> iterator() {
    return null;
  }
}
