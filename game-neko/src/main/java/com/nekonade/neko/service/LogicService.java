package com.nekonade.neko.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LogicService {

    private final Map<Long,String> ctxMap = new ConcurrentHashMap<>();
}
