<cfprocessingdirective pageEncoding="WINDOWS-1251" />

<html>
<head>
<title>�������� ���������</title>
<script src="../../_Functions/SelectCss.js" language="javascript" ></script>
<SCRIPT language=javascript src ="../../_Functions/Print.js"></SCRIPT>
<SCRIPT language=javascript >
<!--
function EditPay_OnClick(){
document.forms[0].action="../IntNew/PayForm.cfm";
}
function Print_OnClick(){
printFrame(window.frames["PrintFrame"]);
}
function Mail_OnClick(){
document.forms[0].action="../../Mail/OutNew/MessageForm.cfm";
}
function Delete_OnClick(){
document.forms[0].action="DocView.cfm";
document.forms[0].onsubmit=Delete_OnSubmit;
}
function Delete_OnSubmit(){
document.forms[0].onsubmit=null;
if (window.confirm("Are you sure want to delete the record?")==true){
   return(true);
   }else return(false);
}
//-->
</SCRIPT>
</head>

<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<cfinclude template="../../TopNavArea.inc">
<!-- Content Area -->
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
      <cfinclude template="../LeftNavArea.inc">
	  <td class="ContentArea"> 
      <cfinclude template="../../ContentNavArea.inc">
	  <iframe src="../../PageToPrint.cfm" scrolling="yes" width="100%" height="200" name="PrintFrame" id="iframe1" ></iframe>
	  <!-- Filter Area -->
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td class="TdBg"></td>
          <td class="TdFilter"> <table width="100%" border="0" cellpadding="7" cellspacing="3">
              <tr>
                  <td align="center"><font color="#CC3300" size="+2"><cfoutput>#CheckSum#</cfoutput></font></td>
              </tr>

			  <tr>
                <td>
									<form name="PayForm" method="POST">
									<cfif IsDefined("URL.Folder")>
										<cfset casevalue=#URL.Folder#>
									<cfelse>
										<cfset casevalue=Referer>
									</cfif>
									<cfswitch expression = #casevalue#>
									<cfcase value = "Drafts">
									<cfif session.ReadOnly GT 0>
			            	<input type="submit" name="CopyPay" value="����������" class="buttonBig" onclick="EditPay_OnClick()">
	  	              <input type="submit" name="Print" value="��������" class="buttonBig" onclick="Print_OnClick()">
									<cfelse>
					<cfif CheckAdvanced EQ 0 AND CheckSum EQ '!!!�������� - ������������ �������!!!' >
			        <cfelse>
	            	<APPLET
	                MAYSCRIPT="MAYSCRIPT" 
	                codebase="../../"  
	                archive="SignApplet.jar"
	                code="SignApplet.class" width="100%" height=35 >
	                <PARAM name="Message" value="<cfoutput>#session.Message64#</cfoutput>">
	                <PARAM name="IDKey" value="<cfoutput>#session.IDKey#</cfoutput>">
	                <PARAM name="DocType" value="100">
	                <PARAM name="DocID" value="<cfoutput>#session.PayID#</cfoutput>">
	                <PARAM name="Disable" value="true">
	                <PARAM name="Debug" value="true">
	                </APPLET>
					</cfif>
										<input type="submit" name="EditPay" value="�������������" class="buttonBig" onclick="EditPay_OnClick()">
	                	<input type="submit" name="Delete" value="�������" class="buttonBig" onclick="Delete_OnClick()">
	                	<input type="submit" name="CopyPay" value="����������" class="buttonBig" onclick="EditPay_OnClick()">
	                	<input type="submit" name="Print" value="��������" class="buttonBig" onclick="Print_OnClick()">
									</cfif>
									</cfcase>
									<cfcase value = "Archive">
			            	<input type="submit" name="CopyPay" value="����������" class="buttonBig" onclick="EditPay_OnClick()">
	  	              <input type="submit" name="Print" value="��������" class="buttonBig" onclick="Print_OnClick()">
									</cfcase>
	            		<cfcase value="InProcess">
	                	<input type="submit" name="Cancel" value="��������" class="button" onclick="Mail_OnClick()">
	                	<input type="submit" name="CopyPay" value="����������" class="buttonBig" onclick="EditPay_OnClick()">
	                	<input type="submit" name="Print" value="��������" class="buttonBig" onclick="Print_OnClick()">
	            		</cfcase>
	            		<cfcase value="Received">
	                	<input type="submit" name="Cancel" value="��������" class="buttonBig" onclick="Mail_OnClick()">
	                	<input type="submit" name="CopyPay" value="����������" class="buttonBig" onclick="EditPay_OnClick()">
	                	<input type="submit" name="Print" value="��������" class="buttonBig" onclick="Print_OnClick()">
	            		</cfcase>
	            		<cfcase value = "Refused">
	                	<input type="submit" name="CopyPay" value="����������" class="buttonBig" onclick="EditPay_OnClick()">
	                	<input type="submit" name="Print" value="��������" class="buttonBig" onclick="Print_OnClick()">
	               	</cfcase>
									<cfcase value = "PayForm.cfm">
										<cfif CheckAdvanced EQ 0 AND CheckSum EQ '!!!�������� - ������������ �������!!!' >
			                            <cfelse>
										<APPLET 
										MAYSCRIPT="MAYSCRIPT" 
										code=SignApplet.class 
										codebase="../../" height=35 width="100%"  
										archive="SignApplet.jar" VIEWASTEXT>
											<PARAM NAME="docid" VALUE="<cfoutput>#session.PayID#</cfoutput>">
											<PARAM name="IDKey" value="<cfoutput>#session.IDKey#</cfoutput>">
											<PARAM NAME="doctype" VALUE="110">
											<PARAM NAME="message" VALUE="<cfoutput>#session.Message64#</cfoutput>">
											<PARAM name="Disable" value="true">
											<PARAM name="Debug" value="false">
										</APPLET>
										</cfif>
		                <input type="submit" name="EditPay" value="�������������" class="buttonBig" onclick="EditPay_OnClick()">
		                <input type="submit" name="Delete" value="�������" class="buttonBig" onclick="Delete_OnClick()">
	  	              <input type="submit" name="CopyPay" value="����������" class="buttonBig" onclick="EditPay_OnClick()">
	    	            <input type="submit" name="Print" value="��������" class="buttonBig" onclick="Print_OnClick()">
							    </cfcase>
									<cfdefaultcase>
			            	<input type="submit" name="CopyPay" value="����������" class="buttonBig" onclick="EditPay_OnClick()">
	  	              <input type="submit" name="Print" value="��������" class="buttonBig" onclick="Print_OnClick()">
    							</cfdefaultcase>
									</cfswitch>
								</form>
								<form name="signform" method="POST" action = "DocView.cfm"></form>
								</td>
              </tr>
            </table></td>
          <td  class="TdBg"></td>
        </tr>
				<tr class="TrBg">
          <td></td>
          <td></td>
          <td></td>
        </tr>
      </table>

  </td>
  </tr>
</table>

</body>
</html>