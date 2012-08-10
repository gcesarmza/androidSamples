package com.gustavogenovese.calculator;

public class CalculatorModel {
	private final static int LIMIT = 10000000;
	
	private int tempBuffer;
	private int operationBuffer;
	private int operation;
	private boolean resetInput = true;
	private boolean error;
	
	public void digitPressed(int digit){
		if (error)
			return;
		
		if (resetInput){
			tempBuffer = 0;
			resetInput = false;
		}
		
		if (digit<0 || digit >9)
			return;
		
		if (tempBuffer < LIMIT)
			tempBuffer = 10*tempBuffer + digit;
	}
	
	public void setOperation(int operation){
		if (error)
			return;
		//0 = +
		//1 = -
		//2 = *
		//3 = /
		if (operation<0 || operation>3)
			return;
		this.operation = operation;
		operationBuffer = tempBuffer;
		resetInput = true;
	}
	
	public void oper(){
		if (error)
			return;
		
		switch (operation){
		case 0:
			tempBuffer = operationBuffer + tempBuffer; 
			break;
			
		case 1:
			tempBuffer = operationBuffer - tempBuffer;
			break;
			
		case 2:
			tempBuffer = operationBuffer * tempBuffer;
			break;
			
		case 3:
			if (tempBuffer == 0){
				error = true;
				return;
			}
			tempBuffer = operationBuffer / tempBuffer;
			break;
		}
		if (tempBuffer >= 10*LIMIT){
			error = true;
		}
	}
	
	public void clear(){
		tempBuffer = 0;
		operationBuffer = 0;
		error = false;
	}
	
	public String getBuffer(){
		return Integer.toString(tempBuffer);
	}

	public boolean isError() {
		return error;
	}
}
