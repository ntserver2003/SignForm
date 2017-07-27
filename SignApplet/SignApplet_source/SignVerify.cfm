<cfheader name = "Expires" value = "#Now()#">
<cfcontent type="text/plain">
<cfif isDefined("session.Message") AND isDefined("session.IDKey") AND
isDefined("session.PayID")>
<!---Public key from database --->
<cfif  isDefined("DocType") AND isDefined("form.Sign") >
<cfstoredproc procedure = "sp_InternetPublicKey"
      dataSource =  #Application.DataSource#>
      <cfprocparam type = "IN" CFSQLType = "CF_SQL_VARCHAR" value = "#session.IDKey#">
	  <cfprocresult name="rs">
</cfstoredproc>
<cfif rs.recordCount GT 0 >
   <cfset PublicKey=rs.PublicKey>
   <cfset Sign = form.Sign>
   <!--- Sign verifier object --->
  <cfobject  action = "Create"  type = "Java"  class = "canopus.esign.SignVerify"  name = "SV">
  <cfset Verify = SV.init(JavaCast("String",session.Message64),JavaCast("String",Sign),JavaCast("String",PublicKey))>
  <cfset rc = Verify.Verify()>
   <cfif rc EQ 0>
   <!--- Database update --->
  	<cfstoredproc procedure = "sp_InternetDocSignatureOK"
      dataSource =  #Application.DataSource#>
      <cfprocparam type = "IN" CFSQLType = "CF_SQL_VARCHAR" value = "#session.IDKey#">
	  <cfprocparam type = "IN" CFSQLType = "CF_SQL_INTEGER" value = "#form.DocType#">
	  <cfprocparam type = "IN" CFSQLType = "CF_SQL_INTEGER" value = "#session.PayID#">
      <cfprocparam type = "IN" CFSQLType = "CF_SQL_VARCHAR" value = "#form.Sign#">
	  <cfprocparam type = "OUT"  CFSQLType = "CF_SQL_INTEGER" variable = "OK" dbVarName = "@OK">
    </cfstoredproc>
    <cfif OK EQ 0 >
	  <cfoutput>OK</cfoutput>
	<cfelse>
	  <cfoutput>DocumentToMessage database error</cfoutput>
    </cfif>
   <cfelseif rc EQ -1>
     <cfset err = Verify.GetLastError()>
     <cfoutput>Verifying internal error: #err#</cfoutput>
   <cfelse>
 	<cfstoredproc procedure = "sp_InternetDecTriesLeft"
      dataSource =  #Application.DataSource#>
      <cfprocparam type = "IN" CFSQLType = "CF_SQL_VARCHAR" value = "#session.IDKey#">
    </cfstoredproc>
    <cfoutput>Signature was not verified!</cfoutput>
   </cfif>
<cfelse>
<cfoutput>Public key database error</cfoutput>
</cfif>
<cfelse>
<cfoutput>SignVerify parameters error</cfoutput>
</cfif>
<cfelse>
<cfoutput>No session</cfoutput>
</cfif>