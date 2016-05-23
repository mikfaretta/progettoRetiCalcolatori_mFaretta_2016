<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>TweetBook - HOME</title>
</head>
<body>
    <form action= "http://localhost:8080/TweetBook_JerseyREST/tweetbook/sendStatus" method="POST">
        Scrivi un messaggio: <input type="text" name="message" id="message">
        <p>
        <input type="submit" value="Invia">
    </form>

</body>
</html>