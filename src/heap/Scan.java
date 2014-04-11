package heap;

import java.io.IOException;

import bufmgr.BufMgrException;
import bufmgr.BufferPoolExceededException;
import bufmgr.HashEntryNotFoundException;
import bufmgr.HashOperationException;
import bufmgr.InvalidFrameNumberException;
import bufmgr.PageNotReadException;
import bufmgr.PagePinnedException;
import bufmgr.PageUnpinnedException;
import bufmgr.ReplacerException;

import global.PageId;
import global.RID;
import global.SystemDefs;

public class Scan {
	private Heapfile hf;
	private HFPage curHfPage;
	private PageId curPageId;
	private RID firstRID;
	private RID curRid;

	public Scan(Heapfile heapfile) throws IOException, ReplacerException,
			HashOperationException, PageUnpinnedException,
			InvalidFrameNumberException, PageNotReadException,
			BufferPoolExceededException, PagePinnedException, BufMgrException {

		hf = heapfile;
		curHfPage = hf.getHead();
		firstRID = hf.getHead().firstRecord();
		curPageId = curHfPage.getCurPage();
		curRid = firstRID;
		SystemDefs.JavabaseBM.pinPage(curPageId, curHfPage, false);
	}

	public Tuple getNext(RID rid) throws ReplacerException,
			HashOperationException, PageUnpinnedException,
			InvalidFrameNumberException, PageNotReadException,
			BufferPoolExceededException, PagePinnedException, BufMgrException,
			HashEntryNotFoundException, IOException, InvalidSlotNumberException {
		if (curRid == null) {
			return null;
		}
		rid.copyRid(curRid);
		Tuple tuple = hf.getRecord(curRid);
		curRid = curHfPage.nextRecord(curRid);
		if (curRid == null) {
			SystemDefs.JavabaseBM.unpinPage(curPageId, true);
			curPageId = curHfPage.getNextPage();
			if (curPageId.pid == -1) {
				curRid = null;
			} else {
				curHfPage.setCurPage(curPageId);
				SystemDefs.JavabaseBM.pinPage(curPageId, curHfPage, false);
				curRid = curHfPage.firstRecord();
			}
		}
		return tuple;
	}

	public void closescan() {
		hf = null;
		firstRID = null;
		curRid = null;
	}


}