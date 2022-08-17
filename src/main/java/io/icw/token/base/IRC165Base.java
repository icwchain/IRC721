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
package io.icw.token.base;

import io.icw.contract.sdk.annotation.Required;
import io.icw.contract.sdk.annotation.View;
import io.icw.token.interfaces.IIRC165;
import io.icw.token.role.Minter;

import java.util.HashSet;
import java.util.Set;

import static io.icw.contract.sdk.Utils.require;

/**
 * @author: PierreLuo
 * @date: 2019-06-13
 */
public class IRC165Base extends Minter implements IIRC165 {

    private Set<String> supportedInterfaces = new HashSet<String>();

    public IRC165Base() {
        supportedInterfaces.add("IIRC165");
    }

    @Override
    @View
    public boolean supportsInterface(@Required String interfaceName) {
        return supportedInterfaces.contains(interfaceName);
    }

    protected void registerInterface(String interfaceName) {
        require(interfaceName != null, "invalid interface name");
        supportedInterfaces.add(interfaceName);
    }
}
