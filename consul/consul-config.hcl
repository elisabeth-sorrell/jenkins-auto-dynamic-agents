consul {
   address = "172.21.0.7:8500"
   retry {
     enabled = true
     attempts = 12
   }
}

template {
   source = "./agent_defs.config.tpl"
   destination = "./agent_defs.config"
}
