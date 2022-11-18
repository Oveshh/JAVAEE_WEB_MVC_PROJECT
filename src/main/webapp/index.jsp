<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
    <head>
        <p>Welcome to the sample application of the Spring frame</p>
    </head>
    <%--获取服务器路径--%>
    <form action="/upload" enctype="multipart/form-data" method="post">
        <input type="file" name="file"/>
        <input type="submit" value="upload">
    </form>
    <style type="text/css">
        *{
            padding: 0;
            margin: 0;
        }

        p {
            margin:0 auto;
            text-align:center;
            font-family:verdana;
            font-size:20px;
            width: 100%;
            height: 40px;
            line-height: 40px;
            color: white;
            top: 25%;
            margin-top: 75px;
        }
        form {
            margin: 0 auto;
            padding-left: 25px;
            padding-right: 25px;
            padding-top: 15px;
            width: 350px;
            height: 350px;
            background: #FFFFFF;
            position: absolute;
            top: 50%;
            left: 50%;
            margin-top: -175px;
            margin-left: -175px;
        }
        body{
            background: #969696;
        }
    </style>
</body>
</html>
