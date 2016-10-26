/*
 * Copyright 2016 Andreas Fester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package afester.javafx.svg;

public enum GradientPolicy {
    // use the gradient transformation matrix as-is, even if it contains 
    // unsupported transformations (skew and scale)
    USE_AS_IS,

    // Use the supported parts of the transformation matrix only (rotation and translation)
    USE_SUPPORTED,
    
    // completely discard the gradient
    DISCARD
}
