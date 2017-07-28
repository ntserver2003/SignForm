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
                    readFile(file);
                } else
                {
                    console.debug("File not selected.");
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
            function setSignatureString(sigVal) {
                $("#signSignature").text(sigVal);
            }
            function getSignatureString(sigVal) {
                return $("#signSignature").text();
            }
            // Sign data
            function doSign() {
                var pwd = $("#signPassword").val();
                var pemKey = getKeyValue();
                console.debug("pemkey = " + pemKey.toString().slice(1, 120) + "...[sliced]");
                var key = null;
                try {
                    key = KEYUTIL.getKey(pemKey, pwd);
                }catch (e){}

                if (key !== null) {
                    /*console.table(pemKey);
                     console.table(key);*/
                    var sig = new KJUR.crypto.Signature({"alg": "SHA1withDSA"});
                    sig.init(key, pwd);
                    var sigString = getSignString();
                    console.debug("Sign string:" + sigString);
                    sig.updateString(sigString);
                    var hSigVal = sig.sign();

                    console.debug("Signature: " + hSigVal);
                    setSignatureString(hSigVal);
                } else
                {
                    var msgError = "Invalid private key or password. Try again";
                    alert(msgError);
                    setSignatureString(msgError);
                }
            }
            // verify data
            function doVerify() {
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
                    var isValid = sig.verify(hSigVal);
                    var isValidMsg ="Signature valid:" + isValid.toString()
                    console.debug(isValidMsg);
                    $("#signIsValid").text(isValidMsg);
                }catch (e){
                    console.error(e.toString());
                    $("#signIsValid").text("Error checking signature.");
                }
            }
