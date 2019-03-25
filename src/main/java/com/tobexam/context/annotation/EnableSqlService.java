package com.tobexam.context.annotation;

import org.springframework.context.annotation.Import;
import com.tobexam.context.SqlServiceContext;

@Import(value=SqlServiceContext.class)
public @interface EnableSqlService {
}