package edu.udacity.java.nano.chat;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

/**
 * WebSocket Server
 *
 * @see ServerEndpoint WebSocket Client
 * @see Session WebSocket Session
 */

@Component
@ServerEndpoint("/chat/{username}")
public class WebSocketChatServer {

	/**
	 * All chat sessions.
	 */
	private static Map<Session, String> onlineSessions = new ConcurrentHashMap<>();

	private static void sendMessageToAll(String msg) {
		for (Session session : onlineSessions.keySet()) {
			try {
				session.getBasicRemote().sendText(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Open connection, 1) add session, 2) add user.
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username) {

		onlineSessions.put(session, username);

		Message message = new Message();

		message.setUsername(username);

		message.setType("ENTER");

		message.setOnlineCount(onlineSessions.size());

		String jsonOutput = JSON.toJSONString(message);

		System.out.println(jsonOutput);

		sendMessageToAll(jsonOutput);

	}

	/**
	 * Send message, 1) get username and session, 2) send message to all.
	 */
	@OnMessage
	public void onMessage(Session session, String jsonStr) {
		Message msg = JSON.parseObject(jsonStr, Message.class);
		msg.setType("CHAT");

		String jsonOutput = JSON.toJSONString(msg);
		sendMessageToAll(jsonOutput);
	}

	/**
	 * Close connection, 1) remove session, 2) update user.
	 */
	@OnClose
	public void onClose(Session session) {
		Message msg = new Message();

		String username = onlineSessions.get(session);
		onlineSessions.remove(session);

		msg.setUsername(username);
		msg.setType("LEAVE");
		msg.setOnlineCount(onlineSessions.size());

		String jsonOutput = JSON.toJSONString(msg);
		sendMessageToAll(jsonOutput);

	}

	/**
	 * Print exception.
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		error.printStackTrace();
	}

	public static Map<Session, String> getOnlineSessions(){
		return onlineSessions;
	}
}
