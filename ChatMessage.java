/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author GusevAlexandr
 * @author DvorjanchikovEvginiy
 */

import java.io.*;
public class ChatMessage implements Serializable {
	static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2 , PUSH = 3 , PUSH_AND_OUT =4;
	private int type;
	private String message;
	
	ChatMessage(int type, String message) {
		this.type = type;
		this.message = message;
	}
	int getType() {
		return type;
	}
	String getMessage() {
		return message;
	}
}