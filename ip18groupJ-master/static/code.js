
$(document).ready(function() {
    var baseUrl = "http://localhost:8080";

    $("#searchbutton").click(function() {
        var t0 = performance.now();
        console.log("Sending request to server.");
        $("#spinner").show();
        $.ajax({
            method: "GET",
            url: baseUrl + "/search",
            data: {query: $('#searchbox').val()}
        }).success( function (data) {
            console.log("Received response " + data);
            if (data.length == 0){
                $("#responsesize").html("<p>" + data.length + " websites found</p>");
            }
            else if(data.length < 50){
                $("#responsesize").html("<p> Showing all " + data.length + " matches</p>");
            }
            else {
                $("#responsesize").html("<p> Showing top 50 results out of " + data.length + " matches</p>");
            }
            var t1 = performance.now();
            var time = (t1-t0)/1000;
            var timeRounded = time.toFixed(2);
            $("#responsetime").html("<p>(" + timeRounded + " seconds) </p>");
            var buffer = "<ul style='list-style-type: none; text-decoration: none;'>\n";
            var counter = 0;
            $.each(data, function(index, value) {
                if (counter != 50) {
                    buffer += "<li><a href=\"" + value.url + "\">" + value.title + "</a><br>" +
                        "<a>" + value.url + "</a></li><br>";
                    counter++;
                }
            });
            buffer += "</ul>";
            $("#urllist").html(buffer);
            $("#spinner").hide();
        });
    });
});
