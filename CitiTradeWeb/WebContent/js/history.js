var historyTable;

$(document).ready(function() {
  getHistory();
});

function getHistory () {
  historyTable = $("#history").DataTable({
    info: false
  });
}