/*
 * Copyright (C) 2012 Jesse Ridgway
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.viewpagerindicator;

public interface IconProvider {
	
	/**
	 * Gets an resource Integer array for icons
	 * @param position
	 * @return Integer array of length 3, left, center, and right icon.
	 */
	public Integer[] getIcon(int child);
	
	/*
	 *Sample implementation: 
	 *public Integer[] getIcon(int child) {
	 * switch(i) {
	 *		case MAIN_ACTIVITY :
	 *			Integer[] mainDrawables = {R.drawable.leftarrow, R.drawable.main_logo, R.drawable.rightarrow};
	 *			return mainDrawables;
	 *		case SETTINGS_ACTIVITY :
	 *			Integer[] settingsDrawables = {R.drawable.settings_icon, R.drawable.settings_logo, R.drawable.settings_icon};
	 *			return settingsDrawables;
	 *		case FRIEND_LIST_ACTIVITY :
	 *			Integer[] friendDrawables = {R.drawable.friends_icon, R.drawable.friends_logo, R.drawable.friends_icon};
	 *			return friendDrawables;
	 *		default :
	 *			throw new IllegalArgumentException("Page does not exist");
	 *		}
	 *}
	 */
}
