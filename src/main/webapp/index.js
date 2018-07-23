
var xhr

function getPlaylist(e) {
    e.preventDefault()
    if (xhr) xhr.abort()
    if ($('#query').val() == "") return

    $('#submit').attr('disabled', true)
    xhr = $.ajax({
        url: '/getPlaylist?' + $('input[name=type]:checked', '#typeForm').val() + '=' + $('#query').val(),
        success: function(data) {
            $("#response").html(getResultText(data))
        },
        error: function(data) {
            $("#response").html('<h3>Could not communicate to server,
                + 'check your internet connection and try again<h3>')
        },
        complete: function() {
            $('#submit').attr('disabled', false)
        }
    })
}

function getResultText(data) {
    var text = '<h3>'
    if (data.success) {
        text += 'It is ' + data.temperature + 'ºC in ' +
            (data.location ? data.location : $('#query').val())
        text += ', here\'s a <a target="_blank" href="' + data.spotify.external_urls.spotify + '">'
        text += data.genre + ' playlist</a> for you!'
        text += getTrackTable(data)
    } else {
        text += data.message
    }
    text += '</h3>'
    return text
}

function getTrackTable(data) {
    var html = '<br/><br/><br/><table>';
    var tracks = data.spotify.tracks.items
    html += '<tr><th>Artist</th><th>Track</th></tr>'
    $.each(tracks, function(i) {
        var track = tracks[i].track
        var artist = (track.artists) ? track.artists[0].name : ""
        html += '<tr><td>'+ artist + '</td><td>' + track.name + '</td></tr>'
    })
    html += '</table>'
    return html
}