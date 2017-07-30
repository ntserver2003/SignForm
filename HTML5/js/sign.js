            // Read key
            // Check for the various File API support.
            if (window.File && window.FileReader && window.FileList && window.Blob) {
                // Great success! All the File APIs are supported.
            } else {
                alert('The File APIs are not fully supported in this browser.');
            }
            $(document).ready(function () {
                $("#signPEMFile").change(handleFileSelect);
            });
            function handleFileSelect(evt) {
                var file = evt.target.files[0];

                if ((typeof file !== "undefined" )
                    && (typeof file.name === "string" || file.name instanceof String)) {
                    console.debug("File select:" + file.name);
                    $("#signFileName").text("Selected file: " + file.name);
                    readFile(file);
                } else
                {
                    console.debug("File not selected.");
                    $("#signFileName").text("File not selected");
                    setKeyValue(null);
                }
            }
            function readFile(file) {
                var reader = new FileReader();
                reader.onload = (function (_file) {
                    return function (_e) {
                        readComplete(_e.target.result);
                    };
                })(file);

                reader.readAsText(file);
            }

            function readComplete(keyData)
            {
                setKeyValue(keyData);
            }

            function setKeyValue(keyData)
            {
                $("#signPrivateKey").val(keyData);
                var signBlock = $("#signBlock");
                if(keyData === null)
                {
                    signBlock.hide();
                }else{
                    signBlock.show();
                }

            }
            function getKeyValue() {
                return $("#signPrivateKey").val();
            }
            function getSignString() {
                return $("#signString").val();
            }
            function setSignatureString(sigVal, err) {
                var sel = $("#signSignature");
                sel.text(sigVal);
                if (err === true)
                {
                    state = "ui-state-error";
                    icon = "ui-icon ui-icon-alert";
                } else {
                    state = "ui-state-highlight";
                    icon = "ui-icon ui-icon-info";
                }
                $("#signSignatureBlock").attr("class", state);
                $("#signSignatureIcon").attr("class", icon);

            }
            function getSignatureString() {
                return $("#signSignature").text();
            }
            
            function setSignatureIsValid(msg, err) {
                var state, icon;
                var sel = $("#signIsValid");
                sel.text(msg);
                if (err === true)
                {
                    state = "ui-state-error";
                    icon = "ui-icon ui-icon-alert";
                } else {
                    state = "ui-state-highlight";
                    icon = "ui-icon ui-icon-info";
                }
                $("#signIsValidBlock").attr("class", state);
                $("#signIsValidIcon").attr("class", icon);
            }

            // Sign data
            function doSign(evt) {
                evt.preventDefault();
                var pwd, pemKey, key;
                pwd = $("#signPassword").val();
                pemKey = getKeyValue();
                console.debug("pemkey = " + pemKey.toString().slice(1, 120) + "...[sliced]");
                try {
                    var sig, sigString, hSigVal;
                    key = KEYUTIL.getKey(pemKey, pwd);
                    sig = new KJUR.crypto.Signature({"alg": "SHA1withDSA"});
                    sig.init(key, pwd);
                    sigString = getSignString();
                    console.debug("Sign string:" + sigString);
                    sig.updateString(sigString);
                    hSigVal = hex2b64(sig.sign());

                    console.debug("Signature: " + hSigVal);
                    setSignatureString(hSigVal);
                }catch (e){
                    var msgError = "Invalid private key or password. Try again";
                    console.error(e.toString());
                    setSignatureString(msgError, true);
                    $( "#signBlock" ).effect( "shake", {"distance": 5} );
                }

            }
            // verify data
            function doVerify(evt) {
                evt.preventDefault();
                var pemKey = getKeyValue();
                var key = null;
                try {
                    key = KEYUTIL.getKey(pemKey);
                    /*console.table(pemKey);
                     console.table(key);*/
                    var sig = new KJUR.crypto.Signature({"alg": "SHA1withDSA"});
                    sig.init(key);
                    var sigString = getSignString();
                    console.debug("Sign string:" + sigString);
                    sig.updateString(sigString);
                    var hSigVal = getSignatureString();

                    console.debug("Signature: " + hSigVal);
                    var isValid = sig.verify(b64tohex(hSigVal));
                    var isValidMsg ="Signature valid:" + isValid.toString();
                    console.debug(isValidMsg);
                    setSignatureIsValid(isValidMsg, isValid !== true);
                }catch (e){
                    console.error(e.toString());
                    setSignatureIsValid("Error checking signature.", true);
                    $( "#signButtonBlock" ).effect( "shake", {"distance": 5} );
                }

            }
