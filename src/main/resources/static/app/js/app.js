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
 * {
 *   "names": {
 *     "x10y60": ["x10y60-label-1"],
 *     "x20y70": ["x20y70-label-1", "x20y70-label-2"],
 *     "x30y80": ["x30y80-label-1"],
 *     "x40y90": ["x40y90-label-1"]
 *   },
 *   "xs": ["x", 10, 20, 30, 40],
 *   "ys": ["y", 60, 70, 80, 90],
 *   "xaxis": "xasis-label",
 *   "yaxis": "yasis-label"
 * }
 *
 * @param data scatter chart data.
 */
ScatterChart.prototype.draw = function(data, xaxis, yaxis) {
    var xaxisText = xaxis || 'Refactoring cost';
    var yaxisText = yaxis || 'Service impact';

    var names = {};
    var xs = {};
    var cols = [];

    for (var i = 0; i < data.length; i++) {
        var d = data[i];
        var repos = d.repository;

        var xindex = repos;
        var yindex = repos + '_y';
        xs[xindex] = yindex;

        var x = d.ys;
        var y = d.xs;
        x.unshift(xindex);
        y.unshift(yindex);
        cols.push(x);
        cols.push(y);

        if (i == 0) {
            names = d.names;
            continue;
        }

        for (var key in d.names) {
            if (key in names) {
                names[key].push(d.names[key]);
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
                label: {
                    text: xaxisText,
                    position: 'outer'
                },
                tick: {
                    fit: false
                }
            },
            y: {
                label: {
                    text: yaxisText,
                    position: 'outer-middle'
                }
            }
        },
        tooltip: {
            contents: function (data, defaultTitleFormat, defaultValueFormat, color) {
                var ns = names['x' + data[0].x + 'y' + data[0].value];
                var contents = '';
                for (var i = 0; i < ns.length; i++) {
                    contents += ns[i] + '\n';
                }

                return '<div style="background-color:rgba(255,255,255,0.8);">' +
                    '<h1 style="font-size:18px;">' +
                    xaxisText + ' = ' + data[0].x + '<br/>' +
                    yaxisText + ' = ' + data[0].value + '</h1>' +
                    '<pre style="font-size:12px;">' + contents + '</pre>' +
                    '</div>';
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
