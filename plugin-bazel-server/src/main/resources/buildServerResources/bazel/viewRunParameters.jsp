<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<jsp:useBean id="params" class="jetbrains.buildServer.bazel.BazelParametersProvider"/>

<c:if test="${not empty propertiesBean.properties[params.runTargetKey]}">
    <div class="parameter">
        Target: <props:displayValue name="${params.runTargetKey}"/>
    </div>
</c:if>