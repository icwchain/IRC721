/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2018 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.icw.token.interfaces;

import io.icw.contract.sdk.annotation.Required;
import io.icw.contract.sdk.annotation.View;

/**
 * @author: PierreLuo
 * @date: 2019-06-13
 */
public interface IIRC165 {
    /**
     * Query if a contract implements an interface
     * @param interfaceName The interface name, as specified in the implementation class of IRC-165.
     *                      eg. interfaceName: ['IIRC165', 'IIRC721', 'IIRC721Enumerable', 'IIRC721Metadata']
     * @return `true` if the contract implements `interfaceName`, `false` otherwise
     */
    @View
    boolean supportsInterface(@Required String interfaceName);
}
