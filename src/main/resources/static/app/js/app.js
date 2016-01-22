/**
 * Created by yoichi.kikuchi on 2016/01/14.
 */
var ScatterChart = function (bindto) {
    this.bindto = bindto;
    this.c3data = null;
    this.chart = null;
    this.default = {
        axis: {
            x: 0,
            y: 0
        }
    };
};

/**
 * Draw scatter chart.
 *
 * data layout requirements:
 * [{
 *   "repository": "REPOS",
 *   "names": {
 *     "x10y60": ["ClassName1a"],
 *     "x20y70": ["ClassName2a", "ClassName2b"],
 *     "x30y80": ["ClassName3a"],
 *     "x40y90": ["ClassName4a"]
 *   },
 *   "xs": [10, 20, 30, 40],
 *   "ys": [60, 70, 80, 90]
 * }]
 *
 * @param data scatter chart data.
 */
ScatterChart.prototype.draw = function(data, xaxis, yaxis) {
    var xaxisText = xaxis || 'Refactoring cost';
    var yaxisText = yaxis || 'Service impact';

    var names = {};
    var repos = {};
    var xs = {};
    var cols = [];

    for (var i = 0; i < data.length; i++) {
        var d = data[i];
        var repo = d.repository;

        var xindex = repo;
        var yindex = repo + '_y';
        xs[xindex] = yindex;

        var x = d.ys;
        var y = d.xs;
        x.unshift(xindex);
        y.unshift(yindex);
        cols.push(x);
        cols.push(y);

        for (var key in d.names) {
            repos[d.names[key]] = repo;

            if (key in names) {
                Array.prototype.push.apply(names[key], d.names[key]);
                continue;
            }

            names[key] = d.names[key];
        }
    }

    this.c3data = {
        bindto: this.bindto,
        data: {
            xs: xs,
            columns: cols,
            type: 'scatter',
            onclick: function (data, element) {
                this.axis.max({
                    x: data.x * 2,
                    y: data.value * 2 + 1
                });
            }
        },
        axis: {
            x: {
                min: 0,
                label: {
                    text: xaxisText,
                    position: 'outer'
                },
                tick: {
                    fit: false
                }
            },
            y: {
                min: 0,
                label: {
                    text: yaxisText,
                    position: 'outer-middle'
                }
            }
        },
        tooltip: {
            contents: function (data, defaultTitleFormat, defaultValueFormat, color) {
                var ns = names['x' + data[0].x + 'y' + data[0].value];
                var contents = '<div style="background-color:rgba(255,255,255,0.9); padding:1rem;"><h1 style="font-size:18px;">' +
                    xaxisText + ' = ' + data[0].x + '<br/>' +
                    yaxisText + ' = ' + data[0].value + '</h1>';

                for (var i = 0; i < ns.length; i++) {
                    contents += '<div><i style="color:' + color(repos[ns[i]]) + '" class="fa fa-square"></i> ' +
                        ns[i].replace(/,/, '<br/>') + '</div>';
                }

                contents += '</div>';

                return contents;
            }
        }
    };

    this.chart = c3.generate(this.c3data);
};

ScatterChart.prototype.reset = function() {
    this.chart.internal.config.axis_x_max = undefined;
    this.chart.internal.config.axis_y_max = undefined;
    this.chart.flush();
};
