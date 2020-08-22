resolvers in ThisBuild += "lightbend-commercial-mvn" at
        "https://repo.lightbend.com/pass/vlG2VqY2fQeEZdpJcOrF28W4Si74DSgA59ZwLrMdiH6H2u13/commercial-releases"
resolvers in ThisBuild += Resolver.url("lightbend-commercial-ivy",
        url("https://repo.lightbend.com/pass/vlG2VqY2fQeEZdpJcOrF28W4Si74DSgA59ZwLrMdiH6H2u13/commercial-releases"))(Resolver.ivyStylePatterns)
