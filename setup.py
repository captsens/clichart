#!/usr/bin/env python3

from setuptools import setup

setup(
    name = "clichart",
    version = "0.6.0b3",
    packages = ['clichart',],
    entry_points = {
        'console_scripts': [
            'aggregate = clichart.aggregate:main',
            'clichart = clichart.clichart:main',
            'derivative = clichart.derivative:main',
            'discretestats = clichart.discretestats:main',
            'histogram = clichart.histogram:main',
            'linestats = clichart.linestats:main',
            'merge = clichart.merge:main',
            'mid = clichart.mid:main',
        ],
    },
    zip_safe = False,
    include_package_data = True,
    package_data = {
        # Include all jar files
        'clichart': ['*.jar', 'lib/*.jar'],
    },
)
