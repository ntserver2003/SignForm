            // Read key
            // Check for the various File API support.
            if (window.File && window.FileReader && window.FileList && window.Blob) {
                // Great success! All the File APIs are supported.
            } else {
                alert('The File APIs are not fully supported in this browser.');
            }
            $(document).ready(function () {
                $("#keyFile").change(handleFileSelect);
            });
            function handleFileSelect(evt) {
                var file = evt.target.files[0];

                if ((typeof file !== "undefined" )
                    && (typeof file.name === "string" || file.name instanceof String)) {
                    console.debug("File select:" + file.name);
                    readFile(file.name);
                } else
                {
                    console.debug("File not selected.");
                    setKeyValue(null);
                    return;
                }
            }
            function readFile(file) {
                var reader = new FileReader();
                reader.onload = (function (_file) {
                    return function (_e) {
                        readComplete(_e.target.result);
                    }
                })

                reader.readAsText(file);
            }

            function readComplete(keyData)
            {
                setKeyValue(keyData);
            }

            function setKeyValue(keyData)
            {
                $("#prvKey1").val(keyData);
            }
            // Sign data
            function doSign() {
                var pemKey = document.form1.prvkey1.value;
                var pwd = document.form1.pwd.value;
                console.debug("pemkey = " + pemKey.toString().slice(1, 50));
                var key = KEYUTIL.getKey( pemKey, pwd);
                /*console.table(pemKey);
                console.table(key);*/
                var sig = new KJUR.crypto.Signature({"alg": "SHA1withDSA"});
                sig.init(key, pwd);
                sig.updateString('aaa');
                var hSigVal = sig.sign();

                console.debug("Signature: " + hSigVal);
                /*var rsa = new RSAKey();
                rsa.readPrivateKeyFromPEMString(document.form1.prvkey1.value);
                var hashAlg = document.form1.hashalg.value;
                var hSig = rsa.signString(document.form1.msgsigned.value, hashAlg);
                document.form1.siggenerated.value = linebrk(hSig, 64);*/
            }
            // initialize
            //var sig = new KJUR.crypto.Signature({"alg": "SHA1withDSA"});
            // initialize for signature generation
            //sig.init(rsaPrivateKey);   // rsaPrivateKey of RSAKey object
            // update data
            //sig.updateString('aaa')
            // calculate signature
            //var sigValueHex = sig.sign()
