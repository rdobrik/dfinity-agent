package com.scaleton.dfinity.agent;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.scaleton.dfinity.agent.hashtree.Label;
import com.scaleton.dfinity.agent.hashtree.LookupResult;
import com.scaleton.dfinity.agent.replicaapi.CallReply;
import com.scaleton.dfinity.agent.replicaapi.Certificate;
import com.scaleton.dfinity.agent.requestid.RequestId;
import com.scaleton.dfinity.candid.Leb128;

public final class ResponseAuthentication {
	
	static RequestStatusResponse lookupRequestStatus(Certificate certificate, RequestId requestId) throws AgentError
	{
		List<Label> pathStatus = new ArrayList<Label>();
		pathStatus.add(new Label("request_status"));			
		pathStatus.add(new Label(requestId.get()));
		pathStatus.add(new Label("status"));
		
		LookupResult result = certificate.tree.lookupPath(pathStatus);
		
		switch(result.status)
		{
			case ABSENT: 
				throw AgentError.create(AgentError.AgentErrorCode.LOOKUP_PATH_ABSENT, pathStatus);
			case UNKNOWN: 
				return new RequestStatusResponse(RequestStatusResponse.InnerStatus.UNKNOWN_STATUS);
			case FOUND: 
			{			
				String status = new String(result.value, StandardCharsets.UTF_8);
				
				switch(status)
				{
					case RequestStatusResponse.DONE_STATUS_VALUE:
						return new RequestStatusResponse(RequestStatusResponse.InnerStatus.DONE_STATUS);
					case RequestStatusResponse.PROCESSING_STATUS_VALUE:
						return new RequestStatusResponse(RequestStatusResponse.InnerStatus.PROCESSING_STATUS);
					case RequestStatusResponse.RECEIVED_STATUS_VALUE:
						return new RequestStatusResponse(RequestStatusResponse.InnerStatus.RECEIVED_STATUS);
					case RequestStatusResponse.REJECTED_STATUS_VALUE:
						return lookupRejection(certificate,requestId);
					case RequestStatusResponse.REPLIED_STATUS_VALUE:
						return lookupReply(certificate,requestId);
					default:
						throw AgentError.create(AgentError.AgentErrorCode.INVALID_REQUEST_STATUS, pathStatus, status);						

				}
			}

			default:
				throw AgentError.create(AgentError.AgentErrorCode.LOOKUP_PATH_ERROR, pathStatus);
		
		}
		
	}
	
	static RequestStatusResponse lookupRejection(Certificate certificate, RequestId requestId)
	{
		Integer rejectCode = lookupRejectCode(certificate, requestId);
		String rejectMessage = lookupRejectMessage(certificate, requestId);
		
		return new RequestStatusResponse(rejectCode, rejectMessage);
		
	}
	
	static Integer lookupRejectCode(Certificate certificate, RequestId requestId)
	{
		List<Label> path = new ArrayList<Label>();
		path.add(new Label("request_status"));			
		path.add(new Label(requestId.get()));
		path.add(new Label("reject_code"));
		
		byte[] code = lookupValue(certificate,path);
		
		return Leb128.readUnsigned(code);		
	}
	
	static String lookupRejectMessage(Certificate certificate, RequestId requestId)
	{
		List<Label> path = new ArrayList<Label>();
		path.add(new Label("request_status"));			
		path.add(new Label(requestId.get()));
		path.add(new Label("reject_message"));
		
		byte[] msg = lookupValue(certificate,path);
		
		return new String(msg, StandardCharsets.UTF_8);
	}	
	
	static RequestStatusResponse lookupReply(Certificate certificate, RequestId requestId)
	{
		List<Label> path = new ArrayList<Label>();
		path.add(new Label("request_status"));			
		path.add(new Label(requestId.get()));
		path.add(new Label("reply"));
		
		byte[] replyData = lookupValue(certificate,path);	
		
		CallReply reply = new CallReply(replyData);
		
		return new RequestStatusResponse(reply);				
	}	
	
	static byte[] lookupValue(Certificate certificate, List<Label> path)
	{
		LookupResult result = certificate.tree.lookupPath(path);
		
		switch(result.status)
		{
			case ABSENT: 
				throw AgentError.create(AgentError.AgentErrorCode.LOOKUP_PATH_ABSENT, path);
			case UNKNOWN: 
				throw AgentError.create(AgentError.AgentErrorCode.LOOKUP_PATH_UNKNOWN, path);
			case FOUND: 
				return result.value;
			case ERROR:
				throw AgentError.create(AgentError.AgentErrorCode.LOOKUP_PATH_ERROR, path);
			default:
				throw AgentError.create(AgentError.AgentErrorCode.LOOKUP_PATH_ERROR, path);
				
		}		
	}
	

}
