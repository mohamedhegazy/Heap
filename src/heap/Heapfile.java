package heap;

import java.io.IOException;
import java.util.LinkedList;

import bufmgr.BufMgrException;
import bufmgr.BufferPoolExceededException;
import bufmgr.HashEntryNotFoundException;
import bufmgr.HashOperationException;
import bufmgr.InvalidFrameNumberException;
import bufmgr.PageNotReadException;
import bufmgr.PagePinnedException;
import bufmgr.PageUnpinnedException;
import bufmgr.ReplacerException;
import chainexception.ChainException;
import diskmgr.DiskMgrException;
import diskmgr.Page;
import global.GlobalConst;
import global.PageId;
import global.RID;
import global.SystemDefs;

public class Heapfile implements GlobalConst {
	private String name;
	private HFPage head;
	private int reccnt = 0;

	public Heapfile(String string) throws IOException, BufferPoolExceededException, HashOperationException, ReplacerException, HashEntryNotFoundException, InvalidFrameNumberException, PagePinnedException, PageUnpinnedException, PageNotReadException, BufMgrException, DiskMgrException {
		// TODO Auto-generated constructor stub
		this.name = string;
		head = new HFPage(new Page());
		PageId id=SystemDefs.JavabaseBM.newPage(head, 1);
		head.setNextPage(new PageId());// pid of next is 0
		head.setPrevPage(new PageId());// pid of prev is 0
		head.setCurPage(id);
		SystemDefs.JavabaseBM.unpinPage(head.getCurPage(), false);

	}

	public RID insertRecord(byte[] byteArray) throws Exception {
		// TODO Auto-generated method stub
		PageId pgId = new PageId();
		RID rid = null;
		HFPage page = new HFPage(new Page());
		if (head.getPrevPage().pid == 0) {// in case the heapfile has no free
											// pages at the beginning
			pgId=SystemDefs.JavabaseBM.newPage(page, 1);
			page.setCurPage(pgId);
			head.setPrevPage(page.getCurPage());// direction to left of free
												// space pages
			page.setPrevPage(head.getCurPage());
			page.setNextPage(new PageId());
			rid = page.insertRecord(byteArray);
			SystemDefs.JavabaseBM.unpinPage(page.getCurPage(), true);
			reccnt++;
			return rid;
		} else {
			pgId = head.getPrevPage();
			while (rid == null) {
				SystemDefs.JavabaseBM.pinPage(pgId, page, false);
				rid = page.insertRecord(byteArray);
				if (rid != null) {// enough space for record was found on page
									// and it was inserted
					reccnt++;
					SystemDefs.JavabaseBM.unpinPage(page.getCurPage(), true);
					return rid;
				}
				SystemDefs.JavabaseBM.unpinPage(page.getCurPage(), false);
				if (page.getNextPage().pid == 0) {// no space for record so we
													// allocate new one
					PageId temPageId = page.getCurPage();
					page = new HFPage();
					SystemDefs.JavabaseBM.newPage(page, 1);
					page.setNextPage(new PageId());
					page.setPrevPage(temPageId);
					rid = page.insertRecord(byteArray);
					reccnt++;
					SystemDefs.JavabaseBM.unpinPage(page.getCurPage(), true);
				}
			}
		}

		return rid;
	}

	public int getRecCnt() {
		// TODO Auto-generated method stub
		return reccnt;
	}

	public Scan openScan() {
		// TODO Auto-generated method stub
		return new Scan();
	}

	public boolean deleteRecord(RID rid) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean updateRecord(RID rid, Tuple newTuple) throws ChainException {
		// TODO Auto-generated method stub
		return false;
	}

	public Tuple getRecord(RID rid) {
		// TODO Auto-generated method stub
		return null;
	}

}
