package heap;

import java.io.IOException;
import java.util.LinkedList;

import javax.xml.bind.annotation.XmlElementDecl.GLOBAL;

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

	public Heapfile(String string) throws IOException,
			BufferPoolExceededException, HashOperationException,
			ReplacerException, HashEntryNotFoundException,
			InvalidFrameNumberException, PagePinnedException,
			PageUnpinnedException, PageNotReadException, BufMgrException,
			DiskMgrException {
		// TODO Auto-generated constructor stub
		this.name = string;
		head = new HFPage();
		PageId id = SystemDefs.JavabaseBM.newPage(head, 1);
		head.init(id, new Page());
		head.setCurPage(id);
		head.setPrevPage(new PageId(GlobalConst.INVALID_PAGE));
		head.setNextPage(new PageId(GlobalConst.INVALID_PAGE));
		SystemDefs.JavabaseBM.unpinPage(head.getCurPage(), false);

	}

	public RID insertRecord(byte[] byteArray) throws Exception {
		// TODO Auto-generated method stub
		RID rid = null;
		PageId id = head.getCurPage();
		HFPage temPage = new HFPage();
		SystemDefs.JavabaseBM.pinPage(id, temPage, false);
		temPage.init(id, new Page(temPage.getpage()));
		while (true) {
			rid = temPage.insertRecord(byteArray);
			if (rid != null) {
				SystemDefs.JavabaseBM.unpinPage(id, true);
				reccnt++;
				return rid;
			} else if (rid == null) {
				SystemDefs.JavabaseBM.unpinPage(id, false);
			}
			if (temPage.getNextPage().pid == GlobalConst.INVALID_PAGE) {
				HFPage new_page = new HFPage();
				id = SystemDefs.JavabaseBM.newPage(new_page, 1);
				new_page.init(id, new Page());
				temPage.setNextPage(id);
				new_page.setPrevPage(temPage.getCurPage());
				new_page.setCurPage(id);
				new_page.setNextPage(new PageId(GlobalConst.INVALID_PAGE));
				rid = new_page.insertRecord(byteArray);
				SystemDefs.JavabaseBM.unpinPage(new_page.getCurPage(), true);
				reccnt++;
				return rid;

			} else {
				id = temPage.getNextPage();
				SystemDefs.JavabaseBM.pinPage(id, temPage, false);

			}
		}
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
