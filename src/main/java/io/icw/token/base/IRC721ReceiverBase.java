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

import io.icw.contract.sdk.Address;
import io.icw.contract.sdk.Msg;
import io.icw.contract.sdk.annotation.Required;
import io.icw.token.interfaces.IIRC721TokenReceiver;
import io.icw.token.model.IRC721TransferRecord;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static io.icw.contract.sdk.Utils.require;

/**
 * @author: PierreLuo
 * @date: 2019-06-13
 */
public class IRC721ReceiverBase implements IIRC721TokenReceiver {

    /**
     * nrc721Address
     * -- tokenId, IRC721TransferRecord
     */
    private Map<Address, Map<BigInteger, IRC721TransferRecord>> nrc721tokenMap = new HashMap<Address, Map<BigInteger, IRC721TransferRecord>>();

    @Override
    public boolean onIRC721Received(@Required Address operator, @Required Address from, @Required BigInteger tokenId, @Required String data) {
        Address nrc721 = Msg.sender();
        if (!nrc721.isContract()) {
            return false;
        }
        Map<BigInteger, IRC721TransferRecord> map = nrc721tokenMap.get(nrc721);
        if (map == null) {
            map = new HashMap<BigInteger, IRC721TransferRecord>();
            map.put(tokenId, new IRC721TransferRecord(operator, from));
            nrc721tokenMap.put(nrc721, map);
            return true;
        }
        IRC721TransferRecord nrc721TransferRecord = map.get(tokenId);
        if (nrc721TransferRecord != null) {
            return false;
        }
        nrc721TransferRecord = new IRC721TransferRecord(operator, from);
        map.put(tokenId, nrc721TransferRecord);
        return true;
    }

    public void transferOtherIRC721(@Required Address nrc721, @Required Address to, @Required BigInteger tokenId) {
        require(!Msg.address().equals(nrc721), "Do nothing by yourself");
        require(nrc721.isContract(), "[" + nrc721.toString() + "] is not contract address");
        Map<BigInteger, IRC721TransferRecord> map = nrc721tokenMap.get(nrc721);
        require(map != null, "No IRC721 token received in [" + nrc721.toString() + "]");
        require(map.remove(tokenId) != null, "No IRC721 token received in [" + nrc721.toString() + "]");

        String methodName = "transferFrom";
        if(to.isContract()) {
            methodName = "safeTransferFrom";
        }
        String[][] args = new String[][]{
                new String[]{Msg.address().toString()},
                new String[]{to.toString()},
                new String[]{tokenId.toString()}};
        nrc721.call(methodName, "(Address from, Address to, BigInteger tokenId) return void", args, BigInteger.ZERO);

    }
}
