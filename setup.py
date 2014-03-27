#!/usr/bin/env python

from setuptools import setup, find_packages

setup(
    name = "clichart",
    version = "0.5.9",
    packages = find_packages(),
    entry_points = {
        'console_scripts': [
            'aggregate = clichart.aggregate:main',
            'clichart = clichart.clichart:main',
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
