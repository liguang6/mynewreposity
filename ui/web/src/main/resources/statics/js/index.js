//生成菜单
var menuItem = Vue.extend({
    name: 'menu-item',
    props:{item:{}},
    template:[
        '<li class="treeview">',
        '	<a v-if="item.type === 0" href="javascript:;" data-href="blank">',
        '		<i v-if="item.icon != null" :class="item.icon"></i>',
        '		<span>{{item.name}}</span>',
        '		<span class="pull-right-container"><i class="fa fa-angle-left pull-right"></i></span>',
        '	</a>',
        '	<ul v-if="item.type === 0" class="treeview-menu">',
        '		<menu-item :item="item" v-for="item in item.list"></menu-item>',
        '	</ul>',

        '	<a v-if="item.type === 1":title="item.name" class="addTabPage" v-if="item.icon != null" :icon="item.icon" href="javascript:"  v-if="item.type === 1 && item.parentId === 0" :data-href="item.url" >',
        '		<i v-if="item.icon != null" :class="item.icon"></i>',
        '		<span>{{item.name}}</span>',
        '	</a>',

        '	<a v-if="item.type === 1":title="item.name" class="addTabPage" v-if="item.icon != null" :icon="item.icon" href="javascript:" v-if="item.type === 1 && item.parentId != 0" :data-href="item.url"  >',
        '		<i v-if="item.icon != null" :class="item.icon"></i><i v-else class="fa fa-circle-o"></i> ',
        '       <span>{{item.name}}</span></a>',
        '</li>'
    ].join('')
});

//iframe自适应
$(window).on('resize', function() {
	var $content = $('.content');
	$content.height($(this).height() - 220);
	$content.find('iframe').each(function() {
		$(this).height($content.height());
	});
}).resize();

//注册菜单组件
Vue.component('menuItem',menuItem);

var vm = new Vue({
	el:'#wms_index',
	data:{
		user:{},
		menuList:{},
		main:"main.html",
		password:'',
		newPassword:'',
        navTitle:"控制台"
	},
	methods: {
		getMenuList: function (event) {
			$.getJSON("sys/menu/nav?_"+$.now(), function(r){
				vm.menuList = r.menuList;
			});
		},
		getUser: function(){
			$.getJSON("sys/user/info?_"+$.now(), function(r){
				vm.user = r.user;
			});
		},
		updatePassword: function(){
			layer.open({
				type: 1,
				skin: 'layui-layer-molv',
				title: "修改密码",
				area: ['550px', '270px'],
				shadeClose: false,
				content: jQuery("#passwordLayer"),
				btn: ['修改','取消'],
				btn1: function (index) {
					var data = "password="+vm.password+"&newPassword="+vm.newPassword;
					$.ajax({
						type: "POST",
					    url: "sys/user/password",
					    data: data,
					    dataType: "json",
					    success: function(result){
							if(result.code == 0){
								layer.close(index);
								layer.alert('修改成功', function(index){
									location.reload();
								});
							}else{
								layer.alert(result.msg);
							}
						}
					});
	            }
			});
		}
	},
	created: function(){
		this.getMenuList();
		this.getUser();
	},
	updated: function(){
		//路由
		var router = new Router();
		routerList(router, vm.menuList);
		router.start();
		var d = window.location.hash.replace("#", "");
		if (d && d != "" && d != window.location.pathname) {
			var c = $('a.addTabPage[data-href="' + d + '"]:eq(0)');
			if (c && c.length > 0) {
				
				c.click()
			} /*else {
				js.addTabPage(null, js.text("tabpanel.newTabPage"), d)
			}*/
		} else {
//			$(".sidebar-menu > li:eq(0):not(.active) > a:eq(0)").click()
		}
		
		$(window).resize();
	}
});



function routerList(router, menuList){
	for(var key in menuList){
		var menu = menuList[key];
		if(menu.type == 0){
			routerList(router, menu.list);
		}else if(menu.type == 1){
			router.add('#'+menu.url, function() {
				var url = window.location.hash;
				
				//替换iframe的url
			    vm.main = url.replace('#', '');  
			    vm.navTitle = $("a[data-href='"+ vm.main+"']").text();
			});
		}
	}
}



/*!
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 *
 * @author ThinkGem
 * @version 2017-4-18
 */
if (self.frameElement && self.frameElement.tagName == "IFRAME") {
	top.location.reload()
} +
function(d) {
	var b = "lte.pushmenu";
	var e = {
		collapseScreenSize: 767,
		expandOnHover: false,
		expandTransitionDelay: 50
	};
	var a = {
		collapsed: ".sidebar-collapse",
		open: ".sidebar-open",
		mainSidebar: ".main-sidebar",
		contentWrapper: ".content-wrapper",
		searchInput: ".sidebar-form .form-control",
		button: '[data-toggle="push-menu"]',
		mini: ".sidebar-mini",
		expanded: ".sidebar-expanded-on-hover",
		layoutFixed: ".fixed"
	};
	var h = {
		collapsed: "sidebar-collapse",
		open: "sidebar-open",
		mini: "sidebar-mini",
		expanded: "sidebar-expanded-on-hover",
		expandFeature: "sidebar-mini-expand-feature",
		layoutFixed: "fixed"
	};
	var i = {
		expanded: "expanded.pushMenu",
		collapsed: "collapsed.pushMenu"
	};
	var g = function(j) {
			this.options = j;
			this.init()
		};
	g.prototype.init = function() {
		if (this.options.expandOnHover || (d("body").is(a.mini + a.layoutFixed))) {
			this.expandOnHover();
			d("body").addClass(h.expandFeature)
		}
		d(a.contentWrapper).click(function() {
			if (d(window).width() <= this.options.collapseScreenSize && d("body").hasClass(h.open)) {
				this.close()
			}
		}.bind(this));
		d(a.searchInput).click(function(j) {
			j.stopPropagation()
		})
	};
	g.prototype.toggle = function() {
		var k = d(window).width();
		var j = !d("body").hasClass(h.collapsed);
		if (k <= this.options.collapseScreenSize) {
			j = d("body").hasClass(h.open)
		}
		if (!j) {
			this.open()
		} else {
			this.close()
		}
		window.setTimeout(function() {
			d(window).resize()
		}, 100)
	};
	g.prototype.open = function() {
		var j = d(window).width();
		if (j > this.options.collapseScreenSize) {
			d("body").removeClass(h.collapsed).trigger(d.Event(i.expanded))
		} else {
			d("body").addClass(h.open).trigger(d.Event(i.expanded))
		}
	};
	g.prototype.close = function() {
		var j = d(window).width();
		if (j > this.options.collapseScreenSize) {
			d("body").addClass(h.collapsed).trigger(d.Event(i.collapsed))
		} else {
			d("body").removeClass(h.open + " " + h.collapsed).trigger(d.Event(i.collapsed))
		}
	};
	g.prototype.expandOnHover = function() {
		d(a.mainSidebar).hover(function() {
			if (d("body").is(a.mini + a.collapsed) && d(window).width() > this.options.collapseScreenSize) {
				this.expand()
			}
		}.bind(this), function() {
			if (d("body").is(a.expanded)) {
				this.collapse()
			}
		}.bind(this))
	};
	g.prototype.expand = function() {
		setTimeout(function() {
			d("body").removeClass(h.collapsed).addClass(h.expanded)
		}, this.options.expandTransitionDelay)
	};
	g.prototype.collapse = function() {
		setTimeout(function() {
			d("body").removeClass(h.expanded).addClass(h.collapsed)
		}, this.options.expandTransitionDelay)
	};

	function f(j) {
		return this.each(function() {
			var m = d(this);
			var l = m.data(b);
			if (!l) {
				var k = d.extend({}, e, m.data(), typeof j == "object" && j);
				m.data(b, (l = new g(k)))
			}
			if (j == "toggle") {
				l.toggle()
			}
		})
	}
	var c = d.fn.pushMenu;
	d.fn.pushMenu = f;
	d.fn.pushMenu.Constructor = g;
	d.fn.pushMenu.noConflict = function() {
		d.fn.pushMenu = c;
		return this
	};
	d(document).on("click", a.button, function(j) {
		j.preventDefault();
		f.call(d(this), "toggle")
	});
	d(function() {
		f.call(d(a.button));
		d(a.button).css({
			cursor: "pointer"
		})
	})
}(jQuery) +
function(e) {
	var b = "lte.tree";
	var f = {
		animationSpeed: 50,
		accordion: true,
		followLink: true,
		trigger: ".treeview a"
	};
	var a = {
		tree: ".tree",
		treeview: ".treeview",
		treeviewMenu: ".treeview-menu",
		open: ".menu-open, .active",
		li: "li",
		data: '[data-widget="tree"]',
		active: ".active"
	};
	var h = {
		open: "menu-open",
		tree: "tree"
	};
	var i = {
		collapsed: "collapsed.tree",
		expanded: "expanded.tree"
	};
	var d = function(k, j) {
			this.element = k;
			this.options = j;
			e(this.element).addClass(h.tree);
			e(a.treeview + a.active, this.element).addClass(h.open);
			this._setUpListeners()
		};
	d.prototype.toggle = function(n, m) {
		var l = n.parent("li").find(a.treeviewMenu);
		var j = n.parent();
		var k = j.hasClass(h.open);
		if (!j.is(a.treeview)) {
			return
		}
		if (!this.options.followLink || n.attr("href") == "#") {
			m.preventDefault()
		}
		//console.info(k)
		if (k) {
			this.collapse(j)
		} else {
			this.expand(j)
		}
	};
	d.prototype.expand = function(l) {
		if (this.options.accordion) {
			var j = l.siblings(a.open);
			var m = j.children(a.treeviewMenu);
			this.collapse(j)
			
		}
		l.addClass("menu-open active");
		var k= l.find(a.treeviewMenu).eq(0)
		//console.info(k.html())
		k.slideDown(this.options.animationSpeed,function(){
			var tab_id=$(".tabpanel_mover").find("li[class='active']").attr("id")
			//alert(tab_id)
			if(tab_id!=undefined){
				var tree_a=$("a[data-tab-id='"+tab_id+"']")
				//alert(tree_a.parent().html())
				if(tree_a.parent().parent().parent().hasClass(a.open)){
					tree_a.parent().addClass("active")
					tree_a.parent().parent().slideDown(0);
				}
				
			}
		});
	};
	d.prototype.collapse = function(k) {
		if(k.children("a").attr("data-href")=="blank"){
			k.removeClass("menu-open active");
		}

		var l = e.Event(i.collapsed);
		k.find(a.treeviewMenu).slideUp(this.options.animationSpeed)
	};
	d.prototype._setUpListeners = function() {
		var j = this;
		e(this.element).on("click", this.options.trigger, function(k) {	
		    var $this = $(this);
			//console.info($this.attr("data-href"))
		    if($this.attr("data-href")!="blank"){
		    	 $(".tree").find(".treeview-item.active").removeClass("treeview-item active")
		    	 $this.parent("li").addClass("treeview-item active")
		    	 $this.parents(".treeview:not(.active)").addClass("menu-open active");
		    }
		    else
	    	//console.info("click"+e(this).html())
			j.toggle(e(this), k)
		    			
		})
	};

	function g(j) {
		return this.each(function() {
			var m = e(this);
			var l = m.data(b);
			if (!l) {
				var k = e.extend({}, f, m.data(), typeof j == "object" && j);
				m.data(b, new d(m, k))
			}
		})
	}
	var c = e.fn.tree;
	e.fn.tree = g;
	e.fn.tree.Constructor = d;
	e.fn.tree.noConflict = function() {
		e.fn.tree = c;
		return this
	};
	e(function() {
		e(a.data).each(function() {
			g.call(e(this))
		})
	})
}(jQuery);
$(function() {
	js.initTabPage("tabpanel", {
		height: function() {
			var f = $(window).height(),
				d = $(".main-header:visible").outerHeight(),
				e = $(".main-footer:visible").outerHeight(),
				c = f - d - e;
			return c < 300 ? 300 : c
		}
	});
	$(window).resize();
	var a = $("#desktopTabPage");
	if (a.size() > 0) {
		js.addTabPage(null, '<i class="fa fa-home"></i> ' + a.data("title"), a.data("url"), false, false)
	}
	var b = $("#modifyPasswordTip");
	if (b.data("message") != "") {
		js.confirm(b.data("message"), function() {
			$("#modifyPassword").click()
		})
	}
	window.isMenuClickFlag = false;
	$(document).on("click", "a.addTabPage", function(c) {
		window.isMenuClickFlag = true
	});
	/*$(window).bind("hashchange", function(f) {
		if (!window.isMenuClickFlag) {
			var d = window.location.hash.replace("#", "");
			if (d && d != "" && d != window.location.pathname) {
				var c = $('a.addTabPage[data-href="' + d + '"]:eq(0)');
				if (c && c.length > 0) {
					c.click()
				} else {
					js.addTabPage(null, js.text("tabpanel.newTabPage"), d)
				}
			} else {
				$(".sidebar-menu > li:eq(0):not(.active) > a:eq(0)").click()
			}
		}
		window.isMenuClickFlag = false
	}).trigger("hashchange");*/
	$("#fullScreen").click(function() {
		if ($(this).data("isOpen") == "true") {
			$(this).data("isOpen", "false");
			if (document.exitFullscreen) {
				document.exitFullscreen()
			} else {
				if (document.msExitFullscreen) {
					document.msExitFullscreen()
				} else {
					if (document.mozCancelFullScreen) {
						document.mozCancelFullScreen()
					} else {
						if (document.webkitCancelFullScreen) {
							document.webkitCancelFullScreen()
						}
					}
				}
			}
		} else {
			$(this).data("isOpen", "true");
			var c = document.documentElement;
			if (c.requestFullscreen) {
				c.requestFullscreen()
			} else {
				if (c.msRequestFullscreen) {
					c.msRequestFullscreen()
				} else {
					if (c.mozRequestFullScreen) {
						c.mozRequestFullScreen()
					} else {
						if (c.webkitRequestFullScreen) {
							c.webkitRequestFullScreen()
						}
					}
				}
			}
		}
		return false
	});
	$("#switchSkin").click(function() {
		js.layer.open({
			type: 2,
			shadeClose: true,
			title: $(this).attr("title"),
			area: ["500px", "390px"],
			content: "/sysSwitchSkin.html",
			success: function(c, d) {
				if ($(js.layer.window).width() < 600 || $(js.layer.window).height() < 490) {
					js.layer.full(d)
				}
			},
		})
	})
	
	var user_type = getUrlKey("user_type");
	if(undefined != user_type && 'undefined' != user_type && null != user_type && user_type=='machine_user'){
		js.addTabPage(null, '<i class="fa fa-plus" style="font-size:12px">机台作业</i>',
				'zzjmes/product/machineOperation.html', 
						true, true);
		$("body").addClass("sidebar-collapse");
	}
	
}); +
function(h) {
	var g = "lte.layout";
	var c = {
		slimscroll: true,
		resetHeight: true
	};
	var b = {
		wrapper: ".wrapper",
		contentWrapper: ".content-wrapper",
		layoutBoxed: ".layout-boxed",
		mainFooter: ".main-footer:visible",
		mainHeader: ".main-header:visible",
		sidebar: ".sidebar",
		controlSidebar: ".control-sidebar",
		fixed: ".fixed",
		sidebarMenu: ".sidebar-menu",
		logo: ".main-header .logo"
	};
	var f = {
		fixed: "fixed",
		holdTransition: "hold-transition"
	};
	var e = function(i) {
			this.options = i;
			this.bindedResize = false;
			this.activate()
		};
	e.prototype.activate = function() {
		this.fix();
		this.fixSidebar();
		h("body").removeClass(f.holdTransition);
		if (!this.bindedResize) {
			h(window).resize(function() {
				this.fix();
				this.fixSidebar();
				h(b.logo + ", " + b.sidebar).one("webkitTransitionEnd otransitionend oTransitionEnd msTransitionEnd transitionend", function() {
					this.fix();
					this.fixSidebar()
				}.bind(this))
			}.bind(this));
			this.bindedResize = true
		}
	};
	e.prototype.fix = function() {};
	e.prototype.fixSidebar = function() {
		if (!h("body").hasClass(f.fixed)) {
			if (typeof h.fn.slimScroll !== "undefined") {
				h(b.sidebar).slimScroll({
					destroy: true
				}).height("auto")
			}
			return
		}
		if (this.options.slimscroll) {
			if (typeof h.fn.slimScroll !== "undefined") {
				h(b.sidebar).slimScroll({
					destroy: true
				}).height("auto");
				h(b.sidebar).slimScroll({
					height: (h(b.contentWrapper).height()) + "px",
					color: "#aaa",
					size: "3px"
				})
			}
		}
	};

	function d(i) {
		return this.each(function() {
			var l = h(this);
			var k = l.data(g);
			if (!k) {
				var j = h.extend({}, c, l.data(), typeof i === "object" && i);
				l.data(g, (k = new e(j)))
			}
			if (typeof i === "string") {
				if (typeof k[i] === "undefined") {
					throw new Error("No method named " + i)
				}
				k[i]()
			}
		})
	}
	var a = h.fn.layout;
	h.fn.layout = d;
	h.fn.layout.Constuctor = e;
	h.fn.layout.noConflict = function() {
		h.fn.layout = a;
		return this
	};
	h(function() {
		d.call(h("body"))
	})
}(jQuery);


function funFromjs(result) {
	var active_iframe=$("#activeIframe").val();
	//alert(active_iframe)
	var childWin=$("#"+active_iframe)[0].contentWindow;
	childWin.funFromjs(result);
	$("#scanResult").val(result)
}

function getUrlKey(name){
	return decodeURIComponent((new RegExp('[?|&]'+name+'='+'([^&;]+?)(&|#|;|$)').exec(location.href)||[,""])[1].replace(/\+/g,'%20'))||null;
}
