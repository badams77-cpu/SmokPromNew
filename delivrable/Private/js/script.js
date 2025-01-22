$(document).ready(function(){
  $('.close_btn').click(function(){
    $('.offcanvas').addClass("offcanvaswidth");
  });
});
  $(document).ready(function () {
            $('#example').DataTable({
                searching: false, // Disable search
      responsive: true, // Ensures responsiveness
      stripeClasses: ['table-striped'],
                pagingType: "simple_numbers", // Simplified pagination
                language: {
                    paginate: {
                        next: 'Next',
                        previous: 'Previous'
                    }
                }
            });
        });




