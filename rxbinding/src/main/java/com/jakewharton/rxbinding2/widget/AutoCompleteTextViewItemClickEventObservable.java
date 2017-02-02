package com.jakewharton.rxbinding2.widget;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

import static com.jakewharton.rxbinding2.internal.Preconditions.isNotOnMainThread;

final class AutoCompleteTextViewItemClickEventObservable
    extends Observable<AdapterViewItemClickEvent> {
  private final AutoCompleteTextView view;

  AutoCompleteTextViewItemClickEventObservable(AutoCompleteTextView view) {
    this.view = view;
  }

  @Override protected void subscribeActual(Observer<? super AdapterViewItemClickEvent> observer) {
    if (isNotOnMainThread(observer)) {
      return;
    }
    Listener listener = new Listener(view, observer);
    observer.onSubscribe(listener);
    view.setOnItemClickListener(listener);
  }

  static final class Listener extends MainThreadDisposable implements OnItemClickListener {
    private final AutoCompleteTextView view;
    private final Observer<? super AdapterViewItemClickEvent> observer;

    Listener(AutoCompleteTextView view, Observer<? super AdapterViewItemClickEvent> observer) {
      this.view = view;
      this.observer = observer;
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      if (!isDisposed()) {
        observer.onNext(AdapterViewItemClickEvent.create(parent, view, position, id));
      }
    }

    @Override protected void onDispose() {
      view.setOnItemClickListener(null);
    }
  }
}
