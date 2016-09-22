/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.react.views.drawer.events;

import android.view.View;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

public class DrawerStateChangedEvent extends Event<DrawerStateChangedEvent> {

  public static final String EVENT_NAME = "topDrawerStateChanged";

  private final int mDrawerState;

  /**
   * See {@link Event#Event(int)}.
   *
   * @param viewTag
   * @param drawerState
   */
  @Deprecated
  public DrawerStateChangedEvent(int viewTag, int drawerState) {
    super(viewTag);
    mDrawerState = drawerState;
  }

  public DrawerStateChangedEvent(View view, int drawerState) {
    super(view);
    mDrawerState = drawerState;
  }

  public int getDrawerState() {
    return mDrawerState;
  }

  @Override
  public String getEventName() {
    return EVENT_NAME;
  }

  @Override
  public short getCoalescingKey() {
    // All events for a given view can be coalesced.
    return 0;
  }

  @Override
  public void dispatch(RCTEventEmitter rctEventEmitter) {
    rctEventEmitter.receiveEvent(getViewTag(), getEventName(), serializeEventData());
  }

  private WritableMap serializeEventData() {
    WritableMap eventData = Arguments.createMap();
    eventData.putDouble("drawerState", getDrawerState());
    return eventData;
  }
}
