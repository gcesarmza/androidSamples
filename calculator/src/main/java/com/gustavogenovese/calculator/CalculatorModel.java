package com.gustavogenovese.calculator;

public class CalculatorModel {
	private int tempBuffer;
	private int operationBuffer;
	private int operation;
	private boolean resetInput = true;
	
	public void digitPressed(int digit){
		if (resetInput){
			tempBuffer = 0;
			resetInput = false;
		}
		if (digit<0 || digit >9)
			return;
		tempBuffer = 10*tempBuffer + digit;
	}
	
	public void setOperation(int operation){
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
			tempBuffer = operationBuffer / tempBuffer;
			break;
		}
	}
	
	public String getBuffer(){
		return Integer.toString(tempBuffer);
	}
}
