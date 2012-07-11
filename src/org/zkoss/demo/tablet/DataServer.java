package org.zkoss.demo.tablet;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.demo.tablet.vo.ContentVO;
import org.zkoss.demo.tablet.vo.ThreadVO;

public class DataServer {
	private static final String HOST = "http://www.zkoss.org";
	private static final String SOURCE_URL = HOST+"/forum/listDiscussion/14";
	private static final String THREAD_START = "<div class=\"discussion-subject\">";
	private static final String TITLE_HEADER = "<a class=\"discussion-title-unread\" href=\"";
	private static final String TITLE_TAIL = ";jsessionid";
	
	//not unique, just in case
	private static final String POST_HEADER = "<td class=\"align-center\">";
	private static final String POST_TAIL = "</td>";
	
	private static final String AUTHOR_HEADER = ",label:'";
	private static final String AUTHOR_TAIL = "'},";
	
	private static final String POPULAR = "<img alt=\"Popular\" title=\"Popular\"";
	private static final String HOT = "<img alt=\"Hot\" title=\"Hot\"";
	private static ArrayList<ThreadVO> allThread = new ArrayList<ThreadVO>();

	public DataServer() {
		//TODO fix fetch algorithm
		if(allThread.size()==0){
			fetchThreadUrl();
		}
	}
	
	private void fetchThreadUrl(){
		String content = Utility.urlToString(SOURCE_URL);
		
		int start = 0, end;
		while((start=content.indexOf(THREAD_START, start)) != -1){
			ThreadVO thread = new ThreadVO();
			
			//XXX "hot" or "popular" will ignore at last thread
			int tmp;
			if((tmp = content.indexOf(POPULAR, start))!=-1){
				if(tmp < content.indexOf(THREAD_START, start+1)){
					thread.setPopular(true);	
				}
			}
			
			if((tmp = content.indexOf(HOT, start))!=-1){
				if(tmp < content.indexOf(THREAD_START, start+1)){
					thread.setHot(true);	
				}
			}
			
			start=content.indexOf(TITLE_HEADER, start);
			end=content.indexOf(TITLE_TAIL, start);
			thread.setUrl(content.substring(start+TITLE_HEADER.length(), end));
			
			start=content.indexOf(">", end);
			end=content.indexOf("</a>", start);
			thread.setTitle(content.substring(start+1, end));

			start=content.indexOf(AUTHOR_HEADER, end);
			end=content.indexOf(AUTHOR_TAIL, start);
			thread.setAuthor(content.substring(start+AUTHOR_HEADER.length(), end));
			
			start=content.indexOf(POST_HEADER, end);
			end=content.indexOf(POST_TAIL, start);
			thread.setPost(Integer.parseInt(content.substring(start+POST_HEADER.length(), end)));

			start=content.indexOf(AUTHOR_HEADER, end);
			end=content.indexOf(AUTHOR_TAIL, start);
			thread.setLastPoster(content.substring(start+AUTHOR_HEADER.length(), end));

			allThread.add(thread);
			start=end;
		}
	}
	
	public List<ThreadVO> getThreadList(){
		return allThread;
	}

	private static final String CONTENT_AUTHOR_HEADER = "<span class=\"author-name\">";
	private static final String CONTENT_AUTHOR_TAIL = "</span>";
	private static final String CONTENT_DATE_HEADER = "<span class=\"comment-date\">";
	private static final String CONTENT_DATE_TAIL = "</span>";
	private static final String CONTENT_PRE_HEADER = "<span class=\"comment-content\">";
	private static final String CONTENT_HEADER = "</span>";
	private static final String CONTENT_TAIL = "</div></div><div class=\"author\">";
	
	public List<ContentVO> getContentList(ThreadVO thread) {
		ArrayList<ContentVO> result = new ArrayList<ContentVO>();
		String content = Utility.urlToString(HOST+thread.getUrl());
		
		int start = 0, end;
		while((start=content.indexOf(CONTENT_AUTHOR_HEADER, start))!=-1){
			ContentVO cvo = new ContentVO();
			
			end=content.indexOf(CONTENT_AUTHOR_TAIL, start);
			cvo.setAuthor(content.substring(start+CONTENT_AUTHOR_HEADER.length(), end));
			
			start=content.indexOf(CONTENT_DATE_HEADER, end);
			end=content.indexOf(CONTENT_DATE_TAIL, start);
			cvo.setDate(content.substring(start+CONTENT_DATE_HEADER.length(), end));
			
			start=content.indexOf(CONTENT_PRE_HEADER, end);
			start=content.indexOf(CONTENT_HEADER, start);
			end=content.indexOf(CONTENT_TAIL, start);
			if(end==-1){	//last post of thread, no more <div class="author">
				end=content.indexOf("</div></div>", start);
				
			}
			cvo.setContent(content.substring(start+CONTENT_HEADER.length(), end));
			result.add(cvo);
			start=end;
		}
		return result;
	}
}