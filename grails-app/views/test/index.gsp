<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>

    <link rel="stylesheet" href="http://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.css" />
    <script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
    <script src="http://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.js"></script>

    <script>

        $(document).on("pagecreate", "#main", function () {

            function statusChanged(e) {
                var id = this.id, value = this.value;
                console.log(id + " has been changed! " + value);
                if (value =="ON") {
                    $.get( "/halloween/test/on", function( data ) {
                        // $( "#result" ).html( data );
                        console.log( "automation started..." );
                    });
                } else {
                    $.get( "/halloween/test/off", function( data ) {
                        // $( "#result" ).html( data );
                        console.log( "automation disabled" );
                    });
                }
            }

            function projectorChanged(e) {
                var id = this.id, value = this.value;
                console.log(id + " has been changed! " + value);
                if (value =="ON") {
                    $.get( "/halloween/test/projectorOn", function( data ) {
                        // $( "#result" ).html( data );
                        console.log( "projector Turned On." );
                    });
                } else {
                    $.get( "/halloween/test/projectorOff", function( data ) {
                        // $( "#result" ).html( data );
                        console.log( "projector Turned Off" );
                    });
                }
            }

            function flipChanged(e) {
                var id = this.id, value = this.value;
                console.log(id + " has been changed! " + value);
                if (value =="ON") {
                    $.get( "/halloween/test/smokeOn", function( data ) {
                        // $( "#result" ).html( data );
                        console.log( "Smoke Turned On." );
                    });
                } else {
                    $.get( "/halloween/test/smokeOff", function( data ) {
                        // $( "#result" ).html( data );
                        console.log( "Smoke Turned Off" );
                    });
                }
            }

            function sliderChanged(e) {
                var id = this.id, value = this.value;
                console.log(id + " has been changed! " + value);
                $.get( "/halloween/test/setDetectionAvgDeviation?startingVolume="+value, function( data ) {
                    // $( "#result" ).html( data );
                    console.log(data);
                });
            }

            $("#status").on("change", statusChanged);

            $("#projector").on("change", projectorChanged);

            $("#flip").on("change", flipChanged);

            $("#mic-slider").on("change", sliderChanged);


        });


    </script>
</head>

<body>
<div data-role="page" id="main">
    <div role="main" class="ui-content">

        <%---
        <div class="ui-field-contain">
            <label for="status">Result:</label>
            <div id="result" name="result"></div>
        </div>
        ---%>

        <div class="ui-field-contain">
            <label for="status">Status:</label>
            <select id="status" data-role="flipswitch">
                <option value="OFF">OFF</option>
                <option value="ON">ON</option>
            </select>
        </div>

        <div class="ui-field-contain">
            <label for="projector">Projector:</label>
            <select id="projector" data-role="flipswitch">
                <option value="OFF">OFF</option>
                <option value="ON">ON</option>
            </select>
        </div>


        <div class="ui-field-contain">
            <label for="flip">Smoke:</label>
            <select id="flip" data-role="flipswitch">
                <option value="OFF">OFF</option>
                <option value="ON">ON</option>
            </select>
        </div>



        <div class="ui-field-contain">
              <label for="mic-slider">Mic Sensetivity:</label>
              <input type="range" name="mic-slider" id="mic-slider" min="-10" max="100" value="${microphoneSensitivity}" data-popup-enabled="true">
        </div>

    </div>
</div>
</body>
</html>