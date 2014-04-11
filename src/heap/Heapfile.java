package heap;

import java.io.IOException;
import java.util.ArrayList;

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
import diskmgr.DuplicateEntryException;
import diskmgr.FileIOException;
import diskmgr.FileNameTooLongException;
import diskmgr.InvalidPageNumberException;
import diskmgr.InvalidRunSizeException;
import diskmgr.OutOfSpaceException;
import diskmgr.Page;
import global.GlobalConst;
import global.PageId;
import global.RID;
import global.SystemDefs;

public class Heapfile implements GlobalConst {
	@SuppressWarnings("unused")
	private String name;
	private HFPage head;
	private int reccnt = 0;
	private ArrayList<HFPageInfo> pages_info;

	public Heapfile(String string) throws IOException,
			BufferPoolExceededException, HashOperationException,
			ReplacerException, HashEntryNotFoundException,
			InvalidFrameNumberException, PagePinnedException,
			PageUnpinnedException, PageNotReadException, BufMgrException,
			DiskMgrException, FileIOException, InvalidPageNumberException,
			FileNameTooLongException, InvalidRunSizeException,
			DuplicateEntryException, OutOfSpaceException {
		// TODO Auto-generated constructor stub
		if (SystemDefs.JavabaseDB.get_file_entry(string) == null) {// heap file
																	// didn't
																	// exist

			this.name = string;
			head = new HFPage();
			PageId id = SystemDefs.JavabaseBM.newPage(head, 1);
			head.init(id, new Page());
			head.setCurPage(id);
			head.setPrevPage(new PageId(GlobalConst.INVALID_PAGE));
			head.setNextPage(new PageId(GlobalConst.INVALID_PAGE));
			pages_info = new ArrayList<HFPageInfo>();
			pages_info.add(new HFPageInfo(head.getUsedPtr(), head
					.getFreeSpace(), head.getType(), head.getSlotCnt(), head
					.getPrevPage(), head.getNextPage(), head.getCurPage()));
			pages_info.add(new HFPageInfo(head.getUsedPtr(), head
					.getFreeSpace(), head.getType(), head.getSlotCnt(), head
					.getPrevPage(), head.getNextPage(), new PageId(
					GlobalConst.INVALID_PAGE)));
			SystemDefs.JavabaseBM.unpinPage(head.getCurPage(), false);
			SystemDefs.JavabaseDB.add_file_entry(string, id);
		} else {// file exists
			this.name = string;
			head = new HFPage();
			PageId id = SystemDefs.JavabaseDB.get_file_entry(string);
			SystemDefs.JavabaseBM.pinPage(id, head, false);
			head.init(id, head);
			SystemDefs.JavabaseBM.unpinPage(head.getCurPage(), false);
		}
	}

	public RID insertRecord(byte[] byteArray) throws Exception {
		// TODO Auto-generated method stub
		if (byteArray.length > MINIBASE_PAGESIZE) {
			throw new SpaceNotAvailableException(null, "Very large record");
		}
		HFPage temPage = new HFPage();
		RID rid = null;
		PageId id = null;
		for (int i = 0; i < pages_info.size(); i++) {
			HFPageInfo info = pages_info.get(i);
			id = info.getCurPage();
			if (info.getCurPage().pid == GlobalConst.INVALID_PAGE) {
				id = SystemDefs.JavabaseBM.newPage(temPage, 1);
				temPage.init(id, new Page());
				rid = temPage.insertRecord(byteArray);
				pages_info
						.add(i,
								new HFPageInfo(temPage.getUsedPtr(), temPage
										.getFreeSpace(), temPage.getType(),
										temPage.getSlotCnt(),
										i == 0 ? new PageId(
												GlobalConst.INVALID_PAGE)
												: pages_info.get(i - 1)
														.getCurPage(),
										new PageId(GlobalConst.INVALID_PAGE),
										temPage.getCurPage()));

				reccnt++;
				SystemDefs.JavabaseBM.unpinPage(id, true);
				return rid;
			} else {
				SystemDefs.JavabaseBM.pinPage(id, temPage, false);
				temPage.setCurPage(info.getCurPage());
				temPage.setNextPage(info.getNextPage());
				temPage.setPrevPage(info.getPrevPage());
				temPage.setSlotCnt(info.getSlotCnt());
				;
				temPage.setFreeSpace(info.getFreeSpace());
				temPage.setUsedPtr(info.getUsedPtr());
				// temPage.setType(info.getType());
				rid = temPage.insertRecord(byteArray);
				if (rid == null) {
					SystemDefs.JavabaseBM.unpinPage(id, false);
					continue;
				} else {
					pages_info.get(i).setUsedPtr(temPage.getUsedPtr());
					pages_info.get(i).setFreeSpace(temPage.getFreeSpace());
					pages_info.get(i).setSlotCnt(temPage.getSlotCnt());
					// pages_info.get(i).setType(temPage.getType());
					reccnt++;
					SystemDefs.JavabaseBM.unpinPage(id, true);
					return rid;
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
		return new Scan(this);
	}

	public boolean deleteRecord(RID rid) throws IOException, ReplacerException,
			HashOperationException, PageUnpinnedException,
			InvalidFrameNumberException, PageNotReadException,
			BufferPoolExceededException, PagePinnedException, BufMgrException,
			HashEntryNotFoundException {
		// TODO Auto-generated method stub
		PageId id = null;
		HFPage temPage = new HFPage();
		for (int i = 0; i < pages_info.size(); i++) {
			HFPageInfo info = pages_info.get(i);
			id = info.getCurPage();
			if (info.getCurPage().pid == GlobalConst.INVALID_PAGE) {
				return false;
			} else {
				SystemDefs.JavabaseBM.pinPage(id, temPage, false);
				temPage.setCurPage(info.getCurPage());
				temPage.setNextPage(info.getNextPage());
				temPage.setPrevPage(info.getPrevPage());
				temPage.setSlotCnt(info.getSlotCnt());
				temPage.setFreeSpace(info.getFreeSpace());
				temPage.setUsedPtr(info.getUsedPtr());
				// temPage.setType(info.getType());
				try {
					temPage.deleteRecord(rid);
					reccnt--;
					SystemDefs.JavabaseBM.unpinPage(id, true);
					return true;
				} catch (Exception e) {
					// TODO: handle exception
					SystemDefs.JavabaseBM.unpinPage(id, false);
				}

			}
		}
		return false;
	}

	public boolean updateRecord(RID rid, Tuple newTuple) throws ChainException,
			IOException {
		// TODO Auto-generated method stub
		PageId id = null;
		HFPage temPage = new HFPage();
		for (int i = 0; i < pages_info.size(); i++) {
			HFPageInfo info = pages_info.get(i);
			id = info.getCurPage();
			if (info.getCurPage().pid == GlobalConst.INVALID_PAGE) {
				return false;
			} else {
				SystemDefs.JavabaseBM.pinPage(id, temPage, false);
				temPage.setCurPage(info.getCurPage());
				temPage.setNextPage(info.getNextPage());
				temPage.setPrevPage(info.getPrevPage());
				temPage.setSlotCnt(info.getSlotCnt());
				temPage.setFreeSpace(info.getFreeSpace());
				temPage.setUsedPtr(info.getUsedPtr());
				// temPage.setType(info.getType());
				try {
					Tuple temp = temPage.getRecord(rid);
					if (temp.getLength() != newTuple.getLength()) {
						throw new InvalidUpdateException(null,
								"Not valid update");
					}
					temp.tupleCopy(newTuple);
					SystemDefs.JavabaseBM.unpinPage(id, true);
					return true;
				} catch (Exception e) {
					// TODO: handle exception
					SystemDefs.JavabaseBM.unpinPage(id, false);
				}

			}
		}
		return false;
	}

	public Tuple getRecord(RID rid) throws IOException, ReplacerException,
			HashOperationException, PageUnpinnedException,
			InvalidFrameNumberException, PageNotReadException,
			BufferPoolExceededException, PagePinnedException, BufMgrException,
			HashEntryNotFoundException {
		// TODO Auto-generated method stub
		PageId id = null;
		HFPage temPage = new HFPage();
		for (int i = 0; i < pages_info.size(); i++) {
			HFPageInfo info = pages_info.get(i);
			id = info.getCurPage();
			if (info.getCurPage().pid == GlobalConst.INVALID_PAGE) {
				return null;
			} else {
				SystemDefs.JavabaseBM.pinPage(id, temPage, false);
				temPage.setCurPage(info.getCurPage());
				temPage.setNextPage(info.getNextPage());
				temPage.setPrevPage(info.getPrevPage());
				temPage.setSlotCnt(info.getSlotCnt());
				temPage.setFreeSpace(info.getFreeSpace());
				temPage.setUsedPtr(info.getUsedPtr());
				// temPage.setType(info.getType());
				try {
					Tuple temp = temPage.getRecord(rid);
					SystemDefs.JavabaseBM.unpinPage(id, true);
					return temp;
				} catch (Exception e) {
					// TODO: handle exception
					SystemDefs.JavabaseBM.unpinPage(id, false);
				}

			}
		}
		return null;
	}
}
