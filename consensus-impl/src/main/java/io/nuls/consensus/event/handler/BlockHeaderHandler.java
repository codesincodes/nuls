/**
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.consensus.event.handler;

import io.nuls.consensus.cache.manager.block.TemporaryCacheManager;
import io.nuls.consensus.entity.GetSmallBlockParam;
import io.nuls.consensus.event.BlockHeaderEvent;
import io.nuls.consensus.event.GetSmallBlockRequest;
import io.nuls.consensus.manager.BlockManager;
import io.nuls.core.chain.entity.Block;
import io.nuls.core.chain.entity.BlockHeader;
import io.nuls.core.context.NulsContext;
import io.nuls.core.utils.log.Log;
import io.nuls.event.bus.handler.AbstractEventHandler;
import io.nuls.event.bus.service.intf.EventBroadcaster;

/**
 * @author facjas
 * @date 2017/11/16
 */
public class BlockHeaderHandler extends AbstractEventHandler<BlockHeaderEvent> {

    private TemporaryCacheManager temporaryCacheManager = TemporaryCacheManager.getInstance();
    private BlockManager blockManager = BlockManager.getInstance();
    private EventBroadcaster eventBroadcaster = NulsContext.getServiceBean(EventBroadcaster.class);

    @Override
    public void onEvent(BlockHeaderEvent event, String fromId) {
        BlockHeader header = event.getEventBody();
        if (null == header) {
            Log.warn("recieved a null blockHeader!");
            return;
        }
        Block block = blockManager.getBlock(header.getHash().getDigestHex());
        if (null != block) {
            return;
        }
        GetSmallBlockRequest request = new GetSmallBlockRequest();
        GetSmallBlockParam param = new GetSmallBlockParam();
        param.setBlockHash(header.getHash());
        request.setEventBody(param);
        eventBroadcaster.sendToNode(request, fromId);
        temporaryCacheManager.cacheBlockHeader(header);
    }
}
