<%@ page import="com.eweware.phototoss.core.PhotoRecord" %>
<%@ page import="java.util.List" %>
<%@ page import="static com.googlecode.objectify.ObjectifyService.ofy" %>
<%@ page import="com.eweware.phototoss.core.TossRecord" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>PhotoToss Home</title>
  <%@include file="includes/stdincludes.jsp" %>
</head>
<body>
<script type="text/javascript">

  var map;
  var homeLoc = {lat: 34, lng: -118};
    var infoWindow = null;

  function initMap() {
    map = new google.maps.Map(document.getElementById('map'), {
        center: homeLoc,
      zoom: 4
    });

      // add markers to map
      $.getJSON( "${pageContext.request.contextPath}/api/recent", function( data ) {
          var myBounds = new google.maps.LatLngBounds();
          var tossTitle = "";
          var myLatLng = new google.maps.LatLng(34, -118);
          var imageUrl;


          $.each( data, function( key, curImage ) {
              if (curImage.tossid == 0) {
                  // root image
                  myLatLng = new google.maps.LatLng(curImage.createdlat, curImage.createdlong);
                  tossTitle = curImage.created;
              } else {
                  // tossed image
                  myLatLng = new google.maps.LatLng(curImage.receivedlat, curImage.receivedlong);
                  tossTitle = curImage.received;
              }
              myBounds = myBounds.extend(myLatLng);

              var marker = new google.maps.Marker({
                  position: myLatLng,
                  map: map,
                  title: tossTitle
              });

              marker.addListener('click', function() {
                  doMarkerClick(marker, curImage);
              });
          });
          map.fitBounds(myBounds);
      });
  }

    function doMarkerClick(theMarker, photoRec) {
        if (infoWindow != null) {
            infoWindow.close();
            infoWindow = null;
        }
        var contentString = '<div id="content">'+
            '<div id="siteNotice">'+
            '</div>'+
            '<div id="bodyContent">'+
            '<a href="${pageContext.request.contextPath}/user/' + photoRec.ownerid + '">' +
            '<img src="https://graph.facebook.com/' + photoRec.ownername + '/picture?type=square";/></a>' +
            '<a href="${pageContext.request.contextPath}/image/' + photoRec.id + '">' +
            '<img height=128 src="' + photoRec.thumbnailurl + '";/></a>' +
            '</div>'+
            '</div>';

        infoWindow = new google.maps.InfoWindow({
            content: contentString
        });
        infoWindow.open(map, theMarker);
    }

</script>
<script async defer
        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCcjZQSZ_8LhmK7jsUQ-atqGoFrnXtpYq4&callback=initMap">
</script>


<%@include file="includes/header.jsp" %>
<div id="map" style="width: 100%; height: 100%"></div>

<h2 style="margin-bottom: 100px">There are <%=ofy().load().type(PhotoRecord.class).count()%> more where those came from!  Install today!</h2>
<%@include file="includes/footer.jsp" %>
</body>
</html>
