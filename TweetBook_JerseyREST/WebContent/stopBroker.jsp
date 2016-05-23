<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="org.apache.activemq.broker.BrokerService" %>

<%

	BrokerService broker = new BrokerService();  
	//configure the broker  
	broker.addConnector("tcp://localhost:61616");  
	broker.stop();  
	
	//use broker.start() to start the service.

%>