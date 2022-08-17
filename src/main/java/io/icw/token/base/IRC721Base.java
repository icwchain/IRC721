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
import io.icw.contract.sdk.annotation.View;
import io.icw.token.interfaces.IIRC721;
import io.icw.token.model.Counter;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static io.icw.contract.sdk.Utils.emit;
import static io.icw.contract.sdk.Utils.require;

/**
 * @author: PierreLuo
 * @date: 2019-06-04
 */
public class IRC721Base extends IRC165Base implements IIRC721 {

    private Map<BigInteger, Address> tokenOwner = new HashMap<BigInteger, Address>();
    private Map<BigInteger, Address> tokenApprovals = new HashMap<BigInteger, Address>();
    private Map<Address, Counter> ownedTokensCount = new HashMap<Address, Counter>();
    private Map<Address, Map<Address, Boolean>> operatorApprovals = new HashMap<Address, Map<Address, Boolean>>();

    public IRC721Base() {
        super.registerInterface("IIRC721");
    }

    @Override
    @View
    public int balanceOf(@Required Address owner) {
        Counter balance = ownedTokensCount.get(owner);
        if(balance == null) {
            return 0;
        }
        return balance.current();
    }

    @Override
    @View
    public Address ownerOf(@Required BigInteger tokenId) {
        Address address = tokenOwner.get(tokenId);
        require(address != null, "IRC721: owner query for nonexistent token");
        return address;
    }

    @Override
    public void safeTransferFrom(@Required Address from, @Required Address to, @Required BigInteger tokenId, @Required String data) {
        transferFrom(from, to, tokenId);
        // checkOnIRC721Received 的作用是当to是合约地址时，那么to这个合约必须实现`onIRC721Received`函数 / data 的作用是附加备注
        require(checkOnIRC721Received(from, to, tokenId, data), "IRC721: transfer to non IRC721Receiver implementer");

    }

    @Override
    public void safeTransferFrom(@Required Address from, @Required Address to, @Required BigInteger tokenId) {
        safeTransferFrom(from, to, tokenId, "");
    }

    @Override
    public void transferFrom(@Required Address from, @Required Address to, @Required BigInteger tokenId) {
        require(isApprovedOrOwner(Msg.sender(), tokenId), "IRC721: transfer caller is not owner nor approved");

        transferFromBase(from, to, tokenId);
    }

    @Override
    public void approve(@Required Address to, @Required BigInteger tokenId) {
        Address owner = ownerOf(tokenId);
        require(!to.equals(owner), "IRC721: approval to current owner");

        require(Msg.sender().equals(owner) || isApprovedForAll(owner, Msg.sender()),
                "IRC721: approve caller is not owner nor approved for all"
        );

        tokenApprovals.put(tokenId, to);
        emit(new Approval(owner, to, tokenId));
    }

    @Override
    public void setApprovalForAll(@Required Address operator, @Required boolean approved) {
        Address sender = Msg.sender();
        require(!operator.equals(sender), "IRC721: approve to caller");

        Map<Address, Boolean> approvalsMap = operatorApprovals.get(sender);
        if(approvalsMap == null) {
            approvalsMap = new HashMap<Address, Boolean>();
            operatorApprovals.put(sender, approvalsMap);
        }
        approvalsMap.put(operator, approved);
        emit(new ApprovalForAll(sender, operator, approved));
    }

    @Override
    @View
    public Address getApproved(@Required BigInteger tokenId) {
        require(exists(tokenId), "IRC721: approved query for nonexistent token");

        return tokenApprovals.get(tokenId);
    }

    @Override
    @View
    public boolean isApprovedForAll(@Required Address owner, @Required Address operator) {
        Map<Address, Boolean> approvalsMap = operatorApprovals.get(owner);
        if(approvalsMap == null) {
            return false;
        }
        Boolean isApproved = approvalsMap.get(operator);
        if(isApproved == null) {
            return false;
        }
        return isApproved;
    }

    protected boolean checkOnIRC721Received(Address from, Address to, BigInteger tokenId, String data) {
        if(!to.isContract()) {
            return true;
        }
        String[][] args = new String[][]{
                new String[]{Msg.sender().toString()},
                new String[]{from.toString()},
                new String[]{tokenId.toString()},
                new String[]{data}};
        String returnValue = to.callWithReturnValue("onIRC721Received", null, args, BigInteger.ZERO);
        return Boolean.valueOf(returnValue);
    }

    protected boolean exists(BigInteger tokenId) {
        Address owner = tokenOwner.get(tokenId);
        return owner != null;
    }

    protected boolean isApprovedOrOwner(Address spender, BigInteger tokenId) {
        require(exists(tokenId), "IRC721: operator query for nonexistent token");
        Address owner = ownerOf(tokenId);
        return (spender.equals(owner) || spender.equals(getApproved(tokenId)) || isApprovedForAll(owner, spender));
    }

    protected void transferFromBase(Address from, Address to, BigInteger tokenId) {
        require(ownerOf(tokenId).equals(from), "IRC721: transfer of token that is not own");

        clearApproval(tokenId);

        ownedTokensCount.get(from).decrement();
        Counter counter = ownedTokensCount.get(to);
        if(counter == null) {
            counter = new Counter();
            ownedTokensCount.put(to, counter);
        }
        counter.increment();

        tokenOwner.put(tokenId, to);

        emit(new Transfer(from, to, tokenId));
    }

    protected void mintBase(Address to, BigInteger tokenId) {
        require(!exists(tokenId), "IRC721: token already minted");

        tokenOwner.put(tokenId, to);
        Counter counter = ownedTokensCount.get(to);
        if(counter == null) {
            counter = new Counter();
            ownedTokensCount.put(to, counter);
        }
        counter.increment();

        emit(new Transfer(null, to, tokenId));
    }

    protected void burnBase(Address owner, BigInteger tokenId) {
        require(ownerOf(tokenId).equals(owner), "IRC721: burn of token that is not own");

        clearApproval(tokenId);

        ownedTokensCount.get(owner).decrement();
        tokenOwner.remove(tokenId);

        emit(new Transfer(owner, null, tokenId));
    }

    protected void burnBase(BigInteger tokenId) {
        burnBase(ownerOf(tokenId), tokenId);
    }

    private void clearApproval(BigInteger tokenId) {
        tokenApprovals.remove(tokenId);
    }
}
