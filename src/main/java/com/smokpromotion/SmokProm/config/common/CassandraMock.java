package com.smokpromotion.SmokProm.config.common;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.context.DriverContext;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@Profile(value="mock_cass")
public class CassandraMock {


    @Bean
    public CassandraState cassandraState() {
        return new CassandraState(false);
    }



    @Bean
    public CqlSession cassandraSession() {
        CqlSession sk = mock(CqlSession.class);
        when(sk.getContext()).thenReturn(mock(DriverContext.class));
        return sk;
    }

    @Bean
    public ReactiveCassandraTemplate cassandraTemplate() {
        ReactiveCassandraTemplate sk = mock(ReactiveCassandraTemplate.class);
 //       when(sk.getContext()).thenReturn(mock(DriverContext.class));
        return sk;
    }


    @Bean
    public CassandraConverter cassandraConverter() {
        CassandraConverter  cc = mock(CassandraConverter.class);
        when(cc.getCodecRegistry()).thenReturn(new CodecRegistry() {
            @NonNull
            @Override
            public <JavaTypeT> TypeCodec<JavaTypeT> codecFor(@NonNull DataType cqlType, @NonNull GenericType<JavaTypeT> javaType) {
                return null;
            }

            @NonNull
            @Override
            public <JavaTypeT> TypeCodec<JavaTypeT> codecFor(@NonNull DataType cqlType) {
                return null;
            }

            @NonNull
            @Override
            public <JavaTypeT> TypeCodec<JavaTypeT> codecFor(@NonNull GenericType<JavaTypeT> javaType) {
                return null;
            }

            @NonNull
            @Override
            public <JavaTypeT> TypeCodec<JavaTypeT> codecFor(@NonNull DataType cqlType, @NonNull JavaTypeT value) {
                return null;
            }

            @NonNull
            @Override
            public <JavaTypeT> TypeCodec<JavaTypeT> codecFor(@NonNull JavaTypeT value) {
                return null;
            }
        });
        return cc;
    }



}
