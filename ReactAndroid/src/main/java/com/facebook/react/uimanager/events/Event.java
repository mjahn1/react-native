/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.react.uimanager.events;

import android.view.View;

import com.facebook.react.common.SystemClock;

import static com.facebook.react.common.TestIdUtil.getOriginalReactTag;

/**
 * A UI event that can be dispatched to JS.
 *
 * For dispatching events {@link EventDispatcher#dispatchEvent} should be used. Once event object
 * is passed to the EventDispatched it should no longer be used as EventDispatcher may decide
 * to recycle that object (by calling {@link #dispose}).
 */
public abstract class Event<T extends Event> {

  private static int sUniqueID = 0;

  private boolean mInitialized;
  private int mViewTag;
  private long mTimestampMs;
  private int mUniqueID = sUniqueID++;

  protected Event() {
  }

  /**
   * Usage of this constructor is discouraged as the viewTag is not gauranteed to represent the
   * associated view.  See {@link com.facebook.react.uimanager.BaseViewManager#setTestId(View, String)}.
   * Use {@link #Event(View)} instead.
   *
   * @param viewTag
   */
  @Deprecated
  protected Event(int viewTag) {
    init(viewTag);
  }

  protected Event(View view) {
    init(getOriginalReactTag(view));
  }

  /**
   * This method needs to be called before event is sent to event dispatcher.
   */
  protected void init(int viewTag) {
    mViewTag = viewTag;
    mTimestampMs = SystemClock.uptimeMillis();
    mInitialized = true;
  }

  protected void init(View view) {
    init(getOriginalReactTag(view));
  }

  /**
   * @return the view id for the view that generated this event
   */
  public final int getViewTag() {
    return mViewTag;
  }

  /**
   * @return the time at which the event happened in the {@link android.os.SystemClock#uptimeMillis}
   * base.
   */
  public final long getTimestampMs() {
    return mTimestampMs;
  }

  /**
   * @return false if this Event can *never* be coalesced
   */
  public boolean canCoalesce() {
    return true;
  }

  /**
   * Given two events, coalesce them into a single event that will be sent to JS instead of two
   * separate events. By default, just chooses the one the is more recent.
   *
   * Two events will only ever try to be coalesced if they have the same event name, view id, and
   * coalescing key.
   */
  public T coalesce(T otherEvent) {
    return (T) (getTimestampMs() > otherEvent.getTimestampMs() ? this : otherEvent);
  }

  /**
   * @return a key used to determine which other events of this type this event can be coalesced
   * with. For example, touch move events should only be coalesced within a single gesture so a
   * coalescing key there would be the unique gesture id.
   */
  public short getCoalescingKey() {
    return 0;
  }

  /**
   * @return The unique id of this event.
   */
  public int getUniqueID() {
    return mUniqueID;
  }

  /**
   * Called when the EventDispatcher is done with an event, either because it was dispatched or
   * because it was coalesced with another Event.
   */
  public void onDispose() {
  }

  /*package*/ boolean isInitialized() {
    return mInitialized;
  }

  /*package*/ final void dispose() {
    mInitialized = false;
    onDispose();
  }

  /**
   * @return the name of this event as registered in JS
   */
  public abstract String getEventName();

  /**
   * Dispatch this event to JS using the given event emitter.
   */
  public abstract void dispatch(RCTEventEmitter rctEventEmitter);
}
