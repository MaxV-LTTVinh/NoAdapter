package vn.tiki.noadapter;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Giang Nguyen on 8/14/16.
 */
public class OnlyAdapter extends RecyclerView.Adapter<AbsViewHolder> {
  private final TypeDeterminer typeDeterminer;
  private final ViewHolderSelector viewHolderSelector;
  private List<?> items = Collections.emptyList();
  private OnItemClickListener onItemClickListener;
  private DiffCallback diffCallback;

  private OnlyAdapter(@NonNull TypeDeterminer typeDeterminer,
                      @NonNull ViewHolderSelector viewHolderSelector,
                      DiffCallback diffCallback) {
    this.typeDeterminer = typeDeterminer;
    this.viewHolderSelector = viewHolderSelector;
    this.diffCallback = diffCallback;
    this.diffCallback.setItems(items);
  }

  private void setOnItemClickListener(@NonNull OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  public void setItems(final List<?> newItems) {
    diffCallback.setItems(items);
    diffCallback.setNewItems(newItems);

    final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

    items.clear();
    items = new ArrayList<>(newItems);

    diffResult.dispatchUpdatesTo(this);
  }

  @Override
  public AbsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return viewHolderSelector.viewHolderForType(parent, viewType);
  }

  @Override
  public int getItemViewType(int position) {
    final Object item = items.get(position);
    return typeDeterminer.typeOf(item);
  }

  @Override
  public void onBindViewHolder(AbsViewHolder holder, int position) {
    final Object item = items.get(position);
    holder.bind(item);
    holder.setOnItemClickListener(onItemClickListener);
  }

  @Override
  public int getItemCount() {
    return items == null ? 0 : items.size();
  }

  public static class Builder {

    private ViewHolderSelector viewHolderSelector;
    private TypeDeterminer typeDeterminer;
    private OnItemClickListener onItemClickListener;
    private DiffCallback diffCallback;

    public Builder typeDeterminer(TypeDeterminer typeDeterminer) {
      this.typeDeterminer = typeDeterminer;
      return this;
    }

    public Builder viewHolderSelector(ViewHolderSelector viewHolderSelector) {
      this.viewHolderSelector = viewHolderSelector;
      return this;
    }

    public Builder onItemClickListener(OnItemClickListener onItemClickListener) {
      this.onItemClickListener = onItemClickListener;
      return this;
    }

    public Builder diffCallback(DiffCallback diffCallback) {
      this.diffCallback = diffCallback;
      return this;
    }

    public OnlyAdapter build() {
      if (typeDeterminer == null) {
        typeDeterminer = new TypeDeterminer() {
          @Override public int typeOf(Object item) {
            return 0;
          }
        };
      }
      if (viewHolderSelector == null) {
        throw new NullPointerException("Null viewHolderSelector");
      }
      if (diffCallback == null) {
        throw new NullPointerException("Null diffCallback");
      }
      final OnlyAdapter adapter = new OnlyAdapter(typeDeterminer, viewHolderSelector, diffCallback);
      if (onItemClickListener != null) {
        adapter.setOnItemClickListener(onItemClickListener);
      }
      return adapter;
    }

  }

}
