
  $(window).on('scroll', function () {
    var menu_area = $('.navbar');
    if ($(window).scrollTop() > 70) {
      menu_area.addClass('sticky_navigation');
    } else {
      menu_area.removeClass('sticky_navigation');

    }
  });


  $('.navbar-nav>li>a').on('click', function(){
    $('.navbar-collapse').collapse('hide');
})

$('.nav-link').click(function () {
        $('body').css('padding-top', '0');
});


var currentYear = new Date().getFullYear();
document.getElementById("currentYear").innerText = currentYear;

 $(document).ready(function() {
    // Close offcanvas and scroll to target section on link click
    $('.navbar-nav a.nav-link').on('click', function() {
      $('#main_navbar').offcanvas('hide'); // Close offcanvas
      var targetSection = $(this).attr('href'); // Get the target section ID from the link
      $('html, body').animate({
        scrollTop: $(targetSection).offset().top - $('.navbar').outerHeight() // Scroll to target section
      }, 100);
    });
  });



    
$('.testi_slider').slick({
  dots: true,
  arrow:true,
  infinite: true,
  autoplay:true,
  speed: 400,
  slidesToShow: 2,
  slidesToScroll: 1,
  responsive: [
    {
      breakpoint: 1024,
      settings: {
        slidesToShow: 2,
        slidesToScroll: 2,
        infinite: true,
      }
    },
    {
      breakpoint: 992,
      settings: {
        slidesToShow: 1,
        slidesToScroll: 1,
      }
    },
    {
      breakpoint: 480,
      settings: {
        arrow:false,
        dots:false,
        slidesToShow: 1,
        slidesToScroll: 1
      }
    }
    // You can unslick at a given breakpoint now by adding:
    // settings: "unslick"
    // instead of a settings object
  ]
});




function myFunction() {
    var x = document.getElementById("myInput");
    if (x.type === "password") {
        x.type = "text";
    } else {
        x.type = "password";
    }
}
function myFunction2() {
    var x = document.getElementById("myInput2");
    if (x.type === "password") {
        x.type = "text";
    } else {
        x.type = "password";
    }
}
function myFunction3() {
    var x = document.getElementById("myInput3");
    if (x.type === "password") {
        x.type = "text";
    } else {
        x.type = "password";
    }
}



$(document).ready(function() {
    // By default, another_address should be shown
    $('.another_address').show();
    
    // Toggle visibility of another_address on checkbox change
    $('#same_address').change(function() {
        if ($(this).is(':checked')) {
            $('.another_address').hide();
        } else {
            $('.another_address').show();
        }
    });
});



 function showTab(tabId) {
    // Remove active class from all nav-links
    document.querySelectorAll('.nav-link').forEach(link => link.classList.remove('active'));
    
    // Remove active and show classes from all tab panes
    document.querySelectorAll('.tab-pane').forEach(tab => tab.classList.remove('active', 'show'));
    
    // Add active class to clicked nav-link and corresponding tab pane
    document.querySelector(`#${tabId}-tab`).classList.add('active');
    document.querySelector(`#${tabId}`).classList.add('active', 'show');
  }