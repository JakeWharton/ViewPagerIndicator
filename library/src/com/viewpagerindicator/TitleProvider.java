/*
 * Copyright (C) 2011 Patrik Akerfeldt
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

/**
 * A TitleProvider provides the title to display according to a view.
 */
public interface TitleProvider {
    /**
     * Returns the title of the view at position
     * @param position
     * @return
     */
    public String getTitle(int position);
}
